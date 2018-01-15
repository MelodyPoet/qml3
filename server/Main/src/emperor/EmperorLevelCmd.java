package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.*;

import java.util.ArrayList;
import java.util.List;

public class EmperorLevelCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorLevelRqst rqst = (EmperorLevelRqst)baseRqst;
        EmperorBaseVo emperorBaseVo = Model.EmperorBaseMap.get((int)rqst.id);
        if(emperorBaseVo == null)return;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.id);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        EmperorLevelUpBaseVo emperorLevelUpBaseVo = Model.EmperorLevelUpBaseMap.get((int)emperorPVo.up+1);
        int limitLevel;
        if(emperorLevelUpBaseVo == null){
            limitLevel = Model.EmperorLevelBaseMap.size();
        }else{
            limitLevel = emperorLevelUpBaseVo.needLevel;
        }
        int maxLevel = Math.min(user.getUserData(UserDataEnum.LEVEL),limitLevel);
        if(emperorPVo.level > maxLevel)return;
        int exp=0;
        int count = rqst.costID.size();
        if(count == 0)return;
        int[] cost = new int[2*count];
        List<Long> costList = (List<Long>)rqst.costID;
        List<Integer> numList = (List<Integer>)rqst.costNum;
        for(int i=0;i<count;i++){
            PropPVo propPVo = user.propModel.getPropInBag(costList.get(i));
            if(propPVo == null)return;
            PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
            if(propBaseVo == null)return;
            int num = numList.get(i);
            exp += propBaseVo.effect[0]*num;
            if(propBaseVo.effect[1] == emperorBaseVo.combatType){
                exp += propBaseVo.effect[2];
            }
            cost[2*i] = propPVo.baseID;
            cost[2*i+1] = num;
        }
        if(user.costUserDataAndPropList(cost,true, ReasonTypeEnum.EMPEROR,null) == false)return;
        emperorPVo.exp += exp;
        int newLevel = 0;
        for(int i=emperorPVo.level;i<maxLevel;i++){
            EmperorLevelBaseVo emperorLevelBaseVo = Model.EmperorLevelBaseMap.get(i);
            if(emperorLevelBaseVo == null)break;
            if(emperorLevelBaseVo.needExp > emperorPVo.exp)break;
            newLevel = i+1;
            emperorPVo.skillPoint += emperorLevelBaseVo.skillPoints;
        }
        if(newLevel != 0){
            emperorPVo.level = (byte) newLevel;
            user.updateZDL();
            EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
            if(emperorAttributePVo != null){
                new EmperorAttributeRspd(client,emperorAttributePVo);
            }
        }
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
    }
}
