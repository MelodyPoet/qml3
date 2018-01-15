package illustrated;

import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import protocol.MonsterGroupPVo;
import protocol.MonsterPVo;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.MonsterIllustratedBaseVo;
import table.PropBaseVo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static comm.Model.MonsterIllustratedBaseMap;

/**
 * Created by admin on 2016/8/16.
 */
public class IllustratedModel extends BaseBlobDeal{
    public ArrayList<MonsterGroupPVo> monsterGroupList = new ArrayList<>();
    public Map<Integer,MonsterGroupPVo> monsterGroupMap = new HashMap<>();
    public int monsterCount = 0;
    public User user;

    public IllustratedModel(User user) {
        this.user = user;
    }

    public void init(){
        for(Map.Entry<Integer,ArrayList<MonsterIllustratedBaseVo>> item : MonsterIllustratedBaseMap.entrySet()){
            if(monsterGroupMap.containsKey(item.getKey()))continue;
            MonsterGroupPVo pVo = new MonsterGroupPVo();
            pVo.mapIndex = (short) (int)item.getKey();
            ArrayList<MonsterPVo> monsterList = new ArrayList<>();
            for(MonsterIllustratedBaseVo vo : item.getValue()){
                MonsterPVo monsterPVo = new MonsterPVo();
                monsterPVo.illustratedID = vo.monsterID;
                monsterPVo.count = 0;
                monsterPVo.needCount = vo.fragmentCount;
                monsterPVo.isAglow = 0;
                monsterList.add(monsterPVo);
                monsterCount++;
            }
            pVo.monsterList = monsterList;
            monsterGroupMap.put(item.getKey(),pVo);
            monsterGroupList.add(pVo);
        }
        user.illustratedModel.saveSqlData();
    }

    public void addSoul(int mapID, PropPVo propPVo){
        PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
        for(Map.Entry<Integer,ArrayList<MonsterIllustratedBaseVo>> item : MonsterIllustratedBaseMap.entrySet()){
            if(JkTools.compare(mapID,item.getValue().get(0).conditionParams[1],item.getValue().get(0).conditionParams[0]) == false)continue;
            if (item.getValue().get(0).conditionParams.length>=4&&JkTools.compare(mapID, item.getValue().get(0).conditionParams[3], item.getValue().get(0).conditionParams[2]) == false)continue;
            for(MonsterPVo vo :  monsterGroupMap.get(item.getKey()).monsterList){
                if(propBaseVo.effect[0] == vo.illustratedID){
                    vo.count += propBaseVo.effect[1] * propPVo.count;
                }
            }
        }
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[monsterCount * 13 + monsterGroupList.size() * (2 + 2) + 2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)monsterGroupList.size());
        for(MonsterGroupPVo monsterGroupPVo : monsterGroupList){
            buffer.putShort((short)monsterGroupPVo.monsterList.size());
            buffer.putShort(monsterGroupPVo.mapIndex);
            for(MonsterPVo monsterPVo : monsterGroupPVo.monsterList){
                monsterPVo.toBytes(buffer);
            }
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        monsterGroupList.clear();
        monsterGroupMap.clear();
        monsterCount = 0;
        if(buffer == null)return;
        int groupCount = buffer.getShort();
        for(int i=0;i<groupCount;i++){
            MonsterGroupPVo monsterGroupPVo = new MonsterGroupPVo();
            int count = buffer.getShort();
            monsterGroupPVo.mapIndex = buffer.getShort();
            monsterGroupPVo.monsterList = new ArrayList<>();
            for(int j=0;j<count;j++){
                MonsterPVo monsterPVo = new MonsterPVo();
                monsterPVo.fromBytes(buffer);
                monsterGroupPVo.monsterList.add(monsterPVo);
                monsterCount++;
            }
            monsterGroupList.add(monsterGroupPVo);
            monsterGroupMap.put((int) monsterGroupPVo.mapIndex,monsterGroupPVo);
        }
        if(monsterGroupMap.size() < Model.MonsterIllustratedBaseMap.size()){
            init();
        }
    }


    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user, AllSql.userSql.FIELD_ILLUSTRATED,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
