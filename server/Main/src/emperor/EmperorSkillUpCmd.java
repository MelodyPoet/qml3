package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.EmperorBaseVo;
import table.GameSetBaseVo;
import table.ReasonTypeEnum;
import table.SkillBaseVo;

import java.util.ArrayList;

public class EmperorSkillUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorSkillUpRqst rqst = (EmperorSkillUpRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.emperorID);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        EmperorBaseVo emperorBaseVo = Model.EmperorBaseMap.get((int)rqst.emperorID);
        if(emperorBaseVo == null)return;
        int[] skillIdArr = null;
        ArrayList<Byte> skillLevel = null;
        if (rqst.type == 0) {
            skillIdArr = emperorBaseVo.skill;
            skillLevel = (ArrayList<Byte>) emperorPVo.skill;
        }else{
            skillIdArr = emperorBaseVo.talent;
            skillLevel = (ArrayList<Byte>) emperorPVo.talent;
        }
        int skillID = skillIdArr[rqst.index];
        ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(skillID);
        if(skillList == null)return;
        int level = skillLevel.get(rqst.index);
        if(level >= skillList.size())return;
        SkillBaseVo learnVo=null;
        if (rqst.type == 0) {
            learnVo = skillList.get(level-1);
        }else{
            learnVo = skillList.get(level);
        }
        if(learnVo.needLevel>emperorPVo.level)return;
        int[] costUserdata = learnVo.costUserdata;
        if(rqst.type == 0){
            int[] cost = new int[costUserdata.length];
            int propID = Model.GameSetBaseMap.get(81).intValue;
            int skillPointCost = 0;
            for(int i=0;i<costUserdata.length;i+=2){
                cost[i] = costUserdata[i];
                if(learnVo.costUserdata[i] == propID){
                    skillPointCost = costUserdata[i+1];
                }else{
                    cost[i+1] = costUserdata[i+1];
                }
            }
            if(emperorPVo.skillPoint < skillPointCost)return;
            if( user.costUserDataAndPropList(cost, true, ReasonTypeEnum.EMPEROR, null)==false)return;
            emperorPVo.skillPoint -= skillPointCost;
        }else{
            if( user.costUserDataAndPropList(costUserdata, true, ReasonTypeEnum.EMPEROR, null)==false)return;
        }
        skillLevel.set(rqst.index,(byte)++level);
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
