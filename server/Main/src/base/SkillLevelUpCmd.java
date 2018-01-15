package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.GeneralSuccessRspd;
import protocol.SkillLevelUpRqst;
import protocol.SkillPVo;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;
import table.SkillBaseVo;
import table.UserDataEnum;

import java.util.ArrayList;

/*
 * Created by jackie on 14-5-6.
 */
public class SkillLevelUpCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SkillLevelUpRqst rqst=(SkillLevelUpRqst) baseRqst;


        SkillPVo skillPvo = user.cacheUserVo.skillModel.skills.get(rqst.baseID);
        if(JkTools.indexOf(user.baseVo.skills, rqst.baseID)<0 ) {
            return;
        }
        ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(rqst.baseID);
        if(skillList==null)return;
        int type = skillList.get(0).type;
        if(skillPvo==null){

            // level check
            skillPvo=new SkillPVo();
            skillPvo.baseID=rqst.baseID;

            if (type==0)
            skillPvo.level=1;
            else
                skillPvo.level=0;

            user.cacheUserVo.skillModel.skills.put(skillPvo.baseID, skillPvo);
        }

        int index = 0;
        if (type==0){
            index = skillPvo.level;
        }else{
            index = skillPvo.level+1;
        }
        if(index >= skillList.size())return;
        if(skillPvo.level>user.getUserData(UserDataEnum.LEVEL))return;
        SkillBaseVo learnVo= skillList.get(index-1);

          if(learnVo.needLevel>user.getUserData(UserDataEnum.LEVEL))return;
       if( user.costUserDataAndPropList(learnVo.costUserdata, true, ReasonTypeEnum.SKILL_LEVEL_UP, null)==false)return;

        skillPvo.level++;
         user.cacheUserVo.skillModel.saveSqlData();

        new GeneralSuccessRspd(client,baseRqst.protocolID);
        user.activationModel.progressBuyAct(MissionConditionEnum.Skill,0);

    }
}
