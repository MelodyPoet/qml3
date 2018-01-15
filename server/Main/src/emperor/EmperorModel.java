package emperor;

import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import protocol.*;
import sqlCmd.AllSql;
import table.EmperorBaseVo;
import table.PropBaseVo;
import table.PropTypeEnum;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EmperorModel extends BaseBlobDeal {
    public User user;
    public HashMap<Byte, EmperorPVo> emperorMap = new HashMap<>();
    public HashMap<Short,AcheivePVo> achieveMap = new HashMap<>();
    public HashMap<Byte, EmperorAttributePVo> attributeMap = new HashMap<>();
    public static int EQUIP_MAX = 4;
    public EmperorModel(User user) {
        this.user = user;
    }
    public void addEmperorUseProp(int count, PropBaseVo propBaseVo){
        addEmperor(count,propBaseVo.effect);
        user.emperorModel.saveSqlData();
    }

    public void addEmperor(int count,int[] effect){
        EmperorPVo emperorPVo;
        byte baseID = (byte) effect[0];
        if(!Model.EmperorBaseMap.containsKey((int)baseID))return;
        HashMap<Byte,EmperorPVo> emperorMap = user.emperorModel.emperorMap;
        count = count * effect[1];
        if(emperorMap.containsKey(baseID)){
            emperorPVo = emperorMap.get(baseID);
            if(effect[1] == 0)return;
        }else{
            emperorPVo =new EmperorPVo();
            emperorPVo.id=baseID;
            emperorMap.put(emperorPVo.id,emperorPVo);
        }
        emperorPVo.count+=count;
        if(effect[2] == 1 && emperorPVo.quality == 0){
            activeEmperor(emperorPVo,effect[1]);
            new EmperorShowRspd(user.client,emperorPVo.id);
        }
        new AddEmperorRspd(user.client,emperorPVo);
    }

    public void activeEmperor(EmperorPVo emperorPVo,int count){
        emperorPVo.level=1;
        EmperorBaseVo emperorBaseVo = Model.EmperorBaseMap.get((int)emperorPVo.id);
        emperorPVo.advance = (byte) 1;
        emperorPVo.quality = (byte) emperorBaseVo.initialQuality;
        emperorPVo.materials = new ArrayList<>();
        emperorPVo.equip = new ArrayList<>();
        for(int i=0;i<EQUIP_MAX;i++){
            emperorPVo.equip.add(0);
        }
        emperorPVo.count -= count;
        emperorPVo.skill = new ArrayList<>();
        for(int i=0;i<emperorBaseVo.skill.length;i++){
            emperorPVo.skill.add((byte)1);
        }
        emperorPVo.talent = new ArrayList<>();
        for(int i=0;i<emperorBaseVo.talent.length;i++){
            emperorPVo.talent.add((byte)0);
        }
    }

    public static boolean isEmperorEquip(PropBaseVo propBaseVo){
        if(propBaseVo.type != PropTypeEnum.EMPEROR_WEAPON && propBaseVo.type != PropTypeEnum.EMPEROR_HEAD && propBaseVo.type != PropTypeEnum.EMPEROR_RING && propBaseVo.type != PropTypeEnum.EMPEROR_FEET)return false;
        return true;
    }

    @Override
    protected byte[] saveDataBytes() {
        int size = 0;
        for(EmperorPVo emperorPVo : emperorMap.values()){
            size += 16+20;
            if(emperorPVo.advance > 0){
                size += emperorPVo.materials.size()*4+emperorPVo.equip.size()*4+emperorPVo.skill.size()+emperorPVo.talent.size();
            }
        }
        size += 3+achieveMap.size()*3;
        byte[] bytes = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) emperorMap.size());
        for(EmperorPVo emperorPVo : emperorMap.values()){
            emperorPVo.toBytes(buffer);
        }
        buffer.putShort((short) achieveMap.size());
        for(AcheivePVo acheivePVo : achieveMap.values()){
            acheivePVo.toBytes(buffer);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int emperorSize = buffer.get();
        for(int i=0;i<emperorSize;i++){
            EmperorPVo emperorPVo = new EmperorPVo();
            emperorPVo.fromBytes(buffer);
            emperorMap.put(emperorPVo.id,emperorPVo);
        }
        int achieveSize = buffer.getShort();
        for(int i=0;i<achieveSize;i++){
            AcheivePVo acheivePVo = new AcheivePVo();
            acheivePVo.fromBytes(buffer);
            achieveMap.put(acheivePVo.achieveID,acheivePVo);
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_EMPEROR,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
