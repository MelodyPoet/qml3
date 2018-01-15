package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.GangSkillBaseVo;
import table.GangUserDataEnum;
import table.ReasonTypeEnum;

/**
 * Created by admin on 2016/10/11.
 */
public class MyGangSkillUpCmd extends BaseRqstCmd{

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MyGangSkillUpRqst rqst = (MyGangSkillUpRqst) baseRqst;
        upGangSkill(client,user,rqst.id);
    }

    private void upGangSkill(Client client,User user,byte id){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!user.gangSkillModel.gangSkillMap.containsKey(id))return;
        GangSkillMsgPVo pVo = user.gangSkillModel.gangSkillMap.get(id);
        if(pVo.isLock == 1)return;
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo gangUserVo = gangVo.users.get(user.guid);
        if(!gangVo.gangBuildMap.containsKey((byte)2))return;
        GangBuildPVo buildPVo = gangVo.gangBuildMap.get((byte)2);
        if(buildPVo.level < pVo.limitLevel)return;
        if(pVo.level+1 > Model.GangSkillBaseMap.get((int)id).size())return;
        boolean flag = false;
            if(user.costUserDataAndProp(12700,1,true, ReasonTypeEnum.GANG_SKILL_UP) == true){
            flag = true;
        }
        if(!flag){
            if(gangUserVo.getGangUserData(GangUserDataEnum.CONTRIBUTION)<pVo.add)return;
            gangUserVo.addGangUserData(GangUserDataEnum.CONTRIBUTION,-pVo.add,true);
        }
        pVo.count += pVo.add;
        if(pVo.count>=pVo.total){
            if(!Model.GangSkillBaseMap.containsKey((int)pVo.id))return;
            pVo.level++;
            pVo.count %= pVo.total;
            GangSkillBaseVo baseVo = Model.GangSkillBaseMap.get((int)pVo.id).get(pVo.level);
            pVo.effect = baseVo.effect;
            pVo.add = baseVo.addValue;
            pVo.total = baseVo.costData;
            pVo.limitLevel = (byte) baseVo.openLimit;
            user.updateZDL();
        }
        gangVo.createGangInfoRspd(client);
        new MyGangSkillUpRspd(client,pVo);
//        new MyGangSkillListRspd(client,buildPVo.level,user.gangSkillModel.gangSkillList);
        user.gangSkillModel.saveSqlData();
    }
}
