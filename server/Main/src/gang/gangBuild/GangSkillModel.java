package gang.gangBuild;

import comm.Model;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import mail.MailModel;
import protocol.*;
import sqlCmd.AllSql;
import table.GangSkillBaseVo;
import table.GangbuildBaseVo;
import table.UserDataEnum;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/10/11.
 */
public class GangSkillModel extends BaseBlobDeal{

    public ArrayList<GangSkillMsgPVo> gangSkillList;
    public Map<Byte, GangSkillMsgPVo> gangSkillMap;
    public User user;

    public GangSkillModel(User user) {
        this.user = user;
        this.gangSkillMap = new HashMap<>();
        this.gangSkillList = new ArrayList<>();
    }

    public void init() {
        for(Map.Entry<Integer,ArrayList<GangSkillBaseVo>> gangSkill: Model.GangSkillBaseMap.entrySet()){
            if(gangSkillMap.containsKey(gangSkill.getKey()))continue;
            GangSkillMsgPVo pVo = new GangSkillMsgPVo();
            pVo.id = (byte)(int)gangSkill.getKey();
            GangSkillBaseVo vo = gangSkill.getValue().get(0);
            if(vo == null)return;
            pVo.level = 0;
            pVo.effect = vo.effect;
            pVo.add = vo.addValue;
            pVo.count = 0;
            pVo.total = vo.costData;
            pVo.limitLevel = (byte) vo.openLimit;
            pVo.isLock = 1;
            gangSkillMap.put(pVo.id,pVo);
            gangSkillList.add(pVo);
        }
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!gangVo.gangBuildMap.containsKey((byte)2))return;
        GangBuildPVo pVo = gangVo.gangBuildMap.get((byte)2);
        ArrayList<GangbuildBaseVo> list = Model.GangbuildBaseMap.get(2);
        for(GangbuildBaseVo baseVo : list){
            if(baseVo.level > pVo.level)break;
            if(baseVo.effect[0] <= 0)continue;
            for(int i : baseVo.effect){
                if(!user.gangSkillModel.gangSkillMap.containsKey((byte)i))continue;
                GangSkillMsgPVo gangSkillMsgPVo = user.gangSkillModel.gangSkillMap.get((byte)i);
                gangSkillMsgPVo.isLock = 0;
            }
        }
        new MyGangSkillListRspd(user.client,pVo.level,user.gangSkillModel.gangSkillList);
        saveSqlData();
    }

    public void clearGangSkill(boolean isLimitJoin,String name){
        int contribute = 0;
        for(GangSkillMsgPVo pVo : gangSkillList){
            if(pVo.isLock == 1)continue;
            ArrayList<GangSkillBaseVo> baseList = Model.GangSkillBaseMap.get((int)pVo.id);
            if(baseList == null)continue;
            for(int i=0;i<pVo.level;i++){
                GangSkillBaseVo baseVo = baseList.get(i);
                if(baseVo == null)continue;
                contribute += baseVo.costData;
            }
            contribute += pVo.count;
        }
        int[] arr = Model.GameSetBaseMap.get(10).intArray;
        int count = contribute*arr[1]/arr[0]/100;
        MailPVo mailPVo = null;
        if(isLimitJoin){
            if(count > 0){
                mailPVo = MailModel.createMail(10001,user.guid);
                ArrayList<AnnexPropPVo> propList = new ArrayList<>();
                AnnexPropPVo pVo = new AnnexPropPVo();
                pVo.propID = 12700;
                pVo.count = count;
                propList.add(pVo);
                mailPVo.prop = propList;
            }
            user.addUserData(UserDataEnum.NEXT_TIME_JOIN_GANG, JkTools.getGameServerTime(null)+24*Model.ONE_HOURS_TIME,true);
        }else{
            if(count > 0){
                mailPVo = MailModel.createMail(10002,user.guid);
                ArrayList<AnnexPropPVo> propList = new ArrayList<>();
                AnnexPropPVo pVo = new AnnexPropPVo();
                pVo.propID = 12700;
                pVo.count = count;
                propList.add(pVo);
                mailPVo.prop = propList;
                mailPVo.msg = mailPVo.msg.replace("{1}",name);
            }else{
                mailPVo = MailModel.createMail(10003,user.guid);
                mailPVo.msg = mailPVo.msg.replace("{1}",name);
            }
        }
        if(mailPVo != null)user.mailModel.sendMail(mailPVo,true);
        gangSkillMap.clear();
        gangSkillList.clear();
        saveSqlData();
        new MyGangSkillListRspd(user.client,(byte)0,gangSkillList);
        user.updateZDL();
    }

    public void checkUp(){
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null){
            ArrayList<GangSkillMsgPVo> list = new ArrayList<>();
            new MyGangSkillListRspd(user.client,(byte) 0,list);
            return;
        }
        if(gangSkillList.size() <= 0){
            user.gangSkillModel.init();
            MailPVo mailPVo = MailModel.createMail(10009,user.guid);
            user.mailModel.sendMail(mailPVo,true);
        }
        if(gangVo.gangBuildMap.containsKey((byte)2)){
            GangBuildPVo pVo = gangVo.gangBuildMap.get((byte)2);
            ArrayList<GangbuildBaseVo> list = Model.GangbuildBaseMap.get(2);
            boolean isSave = false;
            for(GangbuildBaseVo baseVo : list){
                if(baseVo.level > pVo.level)break;
                if(baseVo.effect[0] <= 0)continue;
                for(int i : baseVo.effect){
                    if(!gangSkillMap.containsKey((byte)i))continue;
                    GangSkillMsgPVo gangSkillMsgPVo = gangSkillMap.get((byte)i);
                    if(gangSkillMsgPVo.isLock != 0){
                        gangSkillMsgPVo.isLock = 0;
                        isSave = true;
                    }
                }
            }
            if(isSave){
                saveSqlData();
            }
            new MyGangSkillListRspd(user.client,pVo.level,gangSkillList);
        }
    }

    @Override
    protected byte[] saveDataBytes() {
        byte size = (byte) gangSkillMap.size();
        byte[] bytes = new byte[size*20+1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put(size);
        if(size==0)return bytes;
        for(GangSkillMsgPVo pVo: gangSkillMap.values()){
            pVo.toBytes(buffer);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        gangSkillMap.clear();
        gangSkillList.clear();
        if(buffer == null)return;
        byte count = buffer.get();
        for(byte i=0;i<count;i++){
            GangSkillMsgPVo pVo = new GangSkillMsgPVo();
            pVo.fromBytes(buffer);
            gangSkillMap.put(pVo.id,pVo);
            gangSkillList.add(pVo);
        }
        if(gangSkillMap.size() < Model.GangSkillBaseMap.size()){
            init();
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_GANG_SKILL,saveData());
    }

    @Override
    public void unloadUser() {
        this.user = null;
    }
}
