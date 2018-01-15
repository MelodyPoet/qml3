package dragon;

import base.StoneCountPVoJoin;
import comm.CacheUserVo;
import comm.Model;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseBlobDeal;
import protocol.AddDragonEggRspd;
import protocol.ByteIntPVo;
import protocol.DragonEggPVo;
import protocol.UpdateStoneQualityRspd;
import sqlCmd.AllSql;
import table.DragonEggBaseVo;
import table.PropBaseVo;
import table.UserDataEnum;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by admin on 2016/11/17.
 */
public class DragonEggModel extends BaseBlobDeal{
    public static int SIZE = 5;
    public static int EGG_SIZE = 3;
    public ArrayList<DragonEggVo> dragonEggList;
    public HashMap<Byte,HashSet<Long>> touchMap;
    public HashSet<Long> nowGangUserEggSet;
    public HashSet<Long> theirsEggUpSet;
    public HashSet<Byte> myEggUpSet;

    public DragonEggModel() {
        dragonEggList = new ArrayList<>();
        touchMap = new HashMap<>();
        nowGangUserEggSet = new HashSet<>();
        theirsEggUpSet = new HashSet<>();
        myEggUpSet = new HashSet<>();
    }

    public void addDragonEgg(PropBaseVo propBaseVo){
        if(dragonEggList.size() >= EGG_SIZE)return;
        for(byte i=0;i<EGG_SIZE;i++){
            if(touchMap.containsKey(i))continue;
            DragonEggVo dragonEggVo = new DragonEggVo();
            dragonEggVo.tempID = i;
            dragonEggVo.eggID = (byte) propBaseVo.effect[0];
            DragonEggBaseVo eggBaseVo = Model.DragonEggBaseMap.get((int)dragonEggVo.eggID);
            if(eggBaseVo == null)return;
            dragonEggVo.count = (byte) eggBaseVo.needStroke;
            dragonEggVo.total = dragonEggVo.count;
            dragonEggVo.time = -1;

            dragonEggList.add(dragonEggVo);
            HashSet<Long> set = new HashSet<>();
            touchMap.put(i,set);
            if(user.stoneCountMap.containsKey(dragonEggVo.eggID)){
                ByteIntPVo byteIntPVo = user.stoneCountMap.get(dragonEggVo.eggID);
                byteIntPVo.value ++;
            }else{
                ByteIntPVo byteIntPVo = new ByteIntPVo();
                byteIntPVo.key = dragonEggVo.eggID;
                byteIntPVo.value = 1;
                user.stoneCountMap.put(byteIntPVo.key,byteIntPVo);
            }
            AllSql.userSql.update(user,AllSql.userSql.FIELD_STONE_COUNT,"'"+ StoneCountPVoJoin.instance.joinCollection(user.stoneCountMap.values()) +"'");
            new UpdateStoneQualityRspd(user.client,user.stoneCountMap.values());
            saveSqlData();
            user.addUserData(UserDataEnum.LJ_DRAGON_EGG_COUNT,1,true);

            new AddDragonEggRspd(user.client,dragonEggVo.DragonEggVoToPVo());
            if(user.cacheUserVo.gang.gangVo == null)return;
            GangVo gangVo = user.cacheUserVo.gang.gangVo;
            for(GangUserVo gangUserVo : gangVo.users.values()){
                if(gangUserVo.cacheUserVo.guid == user.guid)continue;
                if(gangUserVo.cacheUserVo.dragonEggModel.nowGangUserEggSet.contains(user.guid)){
                    gangUserVo.cacheUserVo.dragonEggModel.theirsEggUpSet.add(user.guid);
                }
            }
            break;
        }
    }

    public DragonEggVo getEggByTempID(byte tempID){
        DragonEggVo eggVo = null;
        if(touchMap.containsKey(tempID)){
            for(DragonEggVo dragonEggVo : dragonEggList){
                if(tempID == dragonEggVo.tempID){
                    eggVo = dragonEggVo;
                    break;
                }
            }
        }
        return eggVo;
    }

    public ArrayList<DragonEggPVo> getEggPVoList(CacheUserVo cacheUserVo){
        ArrayList<DragonEggPVo> eggPVoList = new ArrayList<>();
        for(DragonEggVo dragonEggVo : cacheUserVo.dragonEggModel.dragonEggList){
            DragonEggPVo dragonEggPVo = dragonEggVo.DragonEggVoToPVo();
            if(cacheUserVo.dragonEggModel.touchMap.containsKey(dragonEggVo.tempID) && cacheUserVo.dragonEggModel.touchMap.get(dragonEggVo.tempID).contains(user.guid)){
                dragonEggPVo.isTouch = 1;
            }
            eggPVoList.add(dragonEggPVo);
        }
        return eggPVoList;
    }

    @Override
    protected byte[] saveDataBytes() {
        int size = 2 + dragonEggList.size() * 8;
        for(HashSet<Long> set : touchMap.values()){
            size += 2 + set.size()*(8);
        }
        byte[] bytes = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) dragonEggList.size());
        for(DragonEggVo pVo : dragonEggList){
            pVo.toBytes(buffer);
        }
        buffer.put((byte) touchMap.size());
        for(Map.Entry<Byte,HashSet<Long>> item : touchMap.entrySet()){
            buffer.put((byte) item.getValue().size());
            for(Long userID : item.getValue()){
                buffer.putLong(userID);
            }
            buffer.put(item.getKey());
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int eggSize = buffer.get();
        for(int i=0;i<eggSize;i++){
            DragonEggVo pVo = new DragonEggVo();
            pVo.fromBytes(buffer);
            dragonEggList.add(pVo);
        }
        int touchSize = buffer.get();
        for(int i=0;i<touchSize;i++){
            HashSet<Long> set = new HashSet<>();
            int size = buffer.get();
            for(int j=0;j<size;j++){
                set.add(buffer.getLong());
            }
            touchMap.put(buffer.get(),set);
        }
    }

    @Override
    public void saveSqlData() {
        if(user == null)return;
        AllSql.userSql.update(user.guid, AllSql.userSql.FIELD_DRAGON_EGG,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
