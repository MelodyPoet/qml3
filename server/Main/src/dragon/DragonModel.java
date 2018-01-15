package dragon;

import base.BaseModel;
import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import protocol.*;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DragonModel  extends BaseBlobDeal  {
//    public Map<Short,DragonLimitTimePVo> dragonLimitTimeMap;
    public Map<Short,ArrayList<DragonTalentPVo>> dragonTalentMap;
    public ArrayList<PropPVo> equipItems;
    public FreePVo moneyLottery;
    public FreePVo diamondLottery;
    public HashSet<Short> dragonAchieveSet;
    public DragonModel(User user){
        this.user=user;
        equipItems=new ArrayList<>();
        moneyLottery=new FreePVo();
        diamondLottery=new FreePVo();
//        dragonLimitTimeMap = new HashMap<>();
        dragonAchieveSet = new HashSet<>();
        dragonTalentMap = new HashMap<>();
    }
    @Override
    public void unloadUser() {
        user=null;
    }
    public void getOneInOpen(){
        if (user.baseVo.defaultDragon<=0)return;
        addDragon((short)user.baseVo.defaultDragon);
        user.cacheUserVo.dragonCacheModel.saveSqlData();
        user.setUserData(UserDataEnum.LotteryDragonNextTime,JkTools.getGameServerTime(user.client)+2*Model.ONE_DAY_TIME,true);
        user.addUserData(UserDataEnum.LJ_LOTTERY_COUNT,1,true);
        user.activationModel.progressBuyAct(MissionConditionEnum.LOTTERY,0);
        ArrayList<PropPVo> dropList = new ArrayList<>();
        PropPVo propPVo = new PropPVo();
        propPVo.baseID = Model.GameSetBaseMap.get(80).intValue;
        propPVo.count = 1;
        dropList.add(propPVo);
        new AwardShowRspd(user.client,dropList);
        new LotteryRspd(user.client,(byte) 0,(byte)0);
    }

    public int getDragonZDL(){
        int[] attribute = new int[AttributeEnum.MAX];
        int[] arr = null;
        for(DragonPVo dragonPVo : user.cacheUserVo.dragonCacheModel.dragonsMap.values()){
            arr = getDragonAttr(dragonPVo);
            if(arr != null)JkTools.arrayAdd(arr,attribute);
        }
        for(short achieveID : dragonAchieveSet){
            DragonAchieveBaseVo baseVo = Model.DragonAchieveBaseMap.get((int)achieveID);
            if(baseVo == null)continue;
            user.addAttribute(baseVo.addAttribute,attribute,null);
        }
        return user.getArrZDL(attribute);
    }

    public int[] getDragonAttr(DragonPVo dragonPVo){
        return getDragonAttr(dragonPVo,100);
    }

    public int[] getDragonAttr(DragonPVo dragonPVo,int percent){
        int[] attribute = new int[AttributeEnum.MAX];
        if(!dragonPVo.isActive)return null;
        DragonBaseVo dragonBaseVo = Model.DragonBaseMap.get((int)dragonPVo.baseID);
        if(dragonBaseVo == null)return null;
        DragonLevelBaseVo levelBaseVo = Model.DragonLevelBaseMap.get((int)dragonPVo.level);
        if(levelBaseVo.properties != null){
            user.addAttribute(levelBaseVo.properties,attribute,null);
        }
        NpcAttributeBaseVo npcAttributeBaseVo = Model.NpcAttributeBaseMap.get(dragonBaseVo.type);
        int[] attr = npcAttributeBaseVo.attributesPower;
        for(int i=0;i<attr.length;i+=2){
            attribute[attr[i]] *= attr[i+1]/100f;
            attribute[attr[i]] += attribute[attr[i]] * dragonBaseVo.addAttr/100f * dragonPVo.advance;
            attribute[attr[i]] *= percent/100f;
        }
        return attribute;
    }

    public DragonPVo addDragonUseProp(int count,PropBaseVo propBaseVo){
        DragonPVo dragonPVo = addDragon(count,propBaseVo.effect);
        user.cacheUserVo.dragonCacheModel.saveSqlData();
        return dragonPVo;
    }

    public ArrayList<PropPVo> drop(int count,int[] dropGroup){
        ArrayList<PropPVo> addList=new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int index=  JkTools.getRandRange(dropGroup,100,2);
            ArrayList<PropPVo> drops = null;
            if(index > -1){
                drops = BaseModel.getDropProps(dropGroup[index+1], 0);
            }
            if(drops == null){
                return null;
            }
//                for(PropPVo propPVo : drops){
//                    ArrayList<DropBaseVo> dropList = Model.DropBaseMap.get(dropGroup[index+1]);
//                    if(dropList==null)return null;
//                    for(DropBaseVo vo : dropList){
//                        if(propPVo.baseID == vo.propID){
//                            propPVo.count = vo.count;
//                            break;
//                        }
//                    }
//                }
            addList.addAll(drops);
        }
        return addList;
    }

    DragonPVo  addDragon(short baseID){
        HashMap<Short,DragonPVo> dragonMap = user.cacheUserVo.dragonCacheModel.dragonsMap;
        DragonPVo dragonPVo= null;
        if(dragonMap.containsKey(baseID)){
            dragonPVo = dragonMap.get(baseID);
            dragonPVo.level=1;
            dragonPVo.isActive = true;
        }else{
            dragonPVo=new DragonPVo();
            dragonPVo.baseID=baseID;
            dragonPVo.level=1;
            dragonPVo.isActive = true;
            dragonPVo.skillLevel =1;
            dragonMap.put(dragonPVo.baseID,dragonPVo);
        }
        user.addUserData(UserDataEnum.LJ_DRAGON_COUNT,1,true);
        new AddDragonRspd(user.client,dragonPVo);
        new DragonShowRspd(user.client,dragonPVo.baseID);
        return dragonPVo;
    }

    DragonPVo  addDragon(int count,int[] effect){
        DragonPVo dragonPVo;
        short baseID = (short)effect[0];
        if(!Model.DragonBaseMap.containsKey((int)baseID))return null;
        HashMap<Short,DragonPVo> dragonsMap = user.cacheUserVo.dragonCacheModel.dragonsMap;
        if(dragonsMap.containsKey(baseID)){
            dragonPVo = dragonsMap.get(baseID);
            if(effect[1] == 0){
//                if(dragonLimitTimeMap.containsKey(baseID)){
//                    dragonLimitTimeMap.remove(baseID);
//                    //通知客户端
//                }
                return null;
            }else{
                dragonPVo.count += count * effect[1];
            }
        }else{
            dragonPVo =new DragonPVo();
            dragonPVo.baseID=baseID;
            if(!Model.DragonAdvanceBaseMap.containsKey((int)baseID))return null;
            DragonAdvanceBaseVo dragonAdvanceBaseVo = Model.DragonAdvanceBaseMap.get((int)baseID).get(0);
            count = count * effect[1];
            if(dragonAdvanceBaseVo.needCount == 1 || effect[2] >= 0 || effect[3] > 0){
                dragonPVo.level=1;
                dragonPVo.advance = (byte) Math.max(0,effect[2]);
                dragonPVo.quality = (byte) effect[3];
                dragonPVo.isActive = true;
                dragonPVo.count=count - effect[1];
                user.addUserData(UserDataEnum.LJ_DRAGON_COUNT,1,true);
                new DragonShowRspd(user.client,dragonPVo.baseID);
            }else{
                dragonPVo.level=0;
                dragonPVo.count=count;
            }
            dragonPVo.skillLevel =1;
//            if(effect.length > 3){
//                DragonLimitTimePVo dragonLimitTimePVo = new DragonLimitTimePVo();
//                dragonLimitTimePVo.dragonID = baseID;
//                dragonLimitTimePVo.limitTime = effect[3]*60;
//                dragonLimitTimePVo.deadTime = JkTools.getGameServerTime(user.client)+dragonLimitTimePVo.limitTime;
//                dragonLimitTimeMap.put(baseID,dragonLimitTimePVo);
//                ArrayList<DragonLimitTimePVo> list = new ArrayList<>();
//                list.add(dragonLimitTimePVo);
//                new AddDragonLimitTimeRspd(user.client,list);
//                saveSqlData();
//            }
            dragonsMap.put(dragonPVo.baseID,dragonPVo);
        }
        return dragonPVo;
    }


//    public void flushDragonList(Client client){
//        if(dragonLimitTimeMap.size() == 0)return;
//        HashSet<Short> set = new HashSet<>();
//        for(DragonLimitTimePVo pVo : dragonLimitTimeMap.values()){
//            if(pVo.deadTime == 0)continue;
//            int time = JkTools.getGameServerTime(client) - pVo.deadTime;
//            if(time>=0){
//                set.add(pVo.dragonID);
//            }
//        }
//        DragonCacheModel dragonCacheModel = user.cacheUserVo.dragonCacheModel;
//        HashMap<Short,DragonPVo> dragonsMap = user.cacheUserVo.dragonCacheModel.dragonsMap;
//        for(short i: set){
//                dragonLimitTimeMap.remove(i);
//                dragonTalentMap.remove(i);
//                dragonsMap.remove(i);
//        }
//        if(set.size()>0){
//            saveSqlData();
//            dragonCacheModel.saveSqlData();
//            ArrayList<DragonPVo> dragonList = new ArrayList<>();
//            for(DragonPVo dragonPVo : dragonCacheModel.dragonsMap.values()){
//                dragonList.add(dragonPVo);
//            }
//            new DragonListRspd(client,dragonList);
//            ArrayList<DragonTalentListPVo> list = new ArrayList<>();
//            for(Map.Entry<Short,ArrayList<DragonTalentPVo>> item : dragonTalentMap.entrySet()){
//                DragonTalentListPVo dragonTalentListPVo = new DragonTalentListPVo();
//                dragonTalentListPVo.dragonID = item.getKey();
//                dragonTalentListPVo.talent = item.getValue();
//                list.add(dragonTalentListPVo);
//            }
//            new DragonTalentRspd(client,list);
//        }
//    }

    @Override
    protected byte[] saveDataBytes() {
        int size = 0;
        for (ArrayList<DragonTalentPVo> list: dragonTalentMap.values()){
            size += list.size()*6+3;
        }
        byte[] bytes=new byte[4+size+dragonAchieveSet.size()*2];
        ByteBuffer buffer=ByteBuffer.wrap(bytes);
        buffer.putShort((short) dragonTalentMap.size());

        for (Map.Entry<Short,ArrayList<DragonTalentPVo>> item: dragonTalentMap.entrySet() ){
            ArrayList<DragonTalentPVo> talent = item.getValue();
            buffer.put((byte) talent.size());
            for(DragonTalentPVo talentPVo : talent){
                talentPVo.toBytes(buffer);
            }
            buffer.putShort(item.getKey());
        }
//        buffer.putShort((short)dragonLimitTimeMap.size());
//        for(DragonLimitTimePVo dragonLimitTimePVo : dragonLimitTimeMap.values()){
//            dragonLimitTimePVo.toBytes(buffer);
//        }
        buffer.putShort((short)dragonAchieveSet.size());
        for(short achieveID : dragonAchieveSet){
            buffer.putShort(achieveID);
        }

        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer==null)return;
        int count=buffer.getShort();

        for (int i = 0; i < count; i++) {
            int size = buffer.get();
            ArrayList<DragonTalentPVo> talent = new ArrayList<>();
            for(int j=0;j<size;j++){
                DragonTalentPVo dragonTalentPVo = new DragonTalentPVo();
                dragonTalentPVo.fromBytes(buffer);
                talent.add(dragonTalentPVo);
            }
            dragonTalentMap.put(buffer.getShort(),talent);
        }
//        int limitTimeCount = buffer.getShort();
//        for(int j=0;j<limitTimeCount;j++){
//            DragonLimitTimePVo dragonLimitTimePVo = new DragonLimitTimePVo();
//            dragonLimitTimePVo.fromBytes(buffer);
//            dragonLimitTimeMap.put(dragonLimitTimePVo.dragonID,dragonLimitTimePVo);
//        }
        int achieveCount = buffer.getShort();
        for(int j=0;j<achieveCount;j++){
            dragonAchieveSet.add(buffer.getShort());
        }

    }


    public void loadDragonStoneEquip(String str){
        if (str == null || str.length() < 2){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short count = buffer.getShort();
        for(int i=0;i<count;i++){
            StonePVo stonePVo = new StonePVo();
            stonePVo.fromBytes(buffer);
            user.cacheUserVo.equipDragonStone.put(stonePVo.index,stonePVo);
        }
    }

    public void saveDragonStoneEquip(){
        int size = user.cacheUserVo.equipDragonStone.size();
        byte[] bytes = new byte[size*5+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)size);
        for(StonePVo stonePVo : user.cacheUserVo.equipDragonStone.values()){
            stonePVo.toBytes(buffer);
        }
        AllSql.userSql.update(user,AllSql.userSql.FIELD_DRAGON_STONE_EQUIP,"'"+new BASE64Encoder().encode(bytes)+"'");
    }
    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user, AllSql.userSql.FIELD_DRAGON, saveData());
    }
}
