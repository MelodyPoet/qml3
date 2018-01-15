package gang.divination;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.DivinationRspd;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/14.
 */
public class DivinationCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
        if(vo == null)return;
        int count = vo.getGangUserData(GangUserDataEnum.DIVINATION_COUNT);
        if(count <= 0)return;
        if(GameSetModel.checkTime(1))return;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        int id = vipBaseVo.divinationCount - count;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int)ModuleUIEnum.DIVINATION);
        if(costList == null)return;
        if(id < 0)return;
        CommCostBaseVo costBaseVo = costList.get(id);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.DIVINATION,null) == false)return;
        int rand = JkTools.getRandRange(Model.GameSetBaseMap.get(35).intArray,100,2);
        int luckValue = 0;
        byte multiple =  (byte)Model.GameSetBaseMap.get(35).intArray[rand+1];
        if(rand > -1){
            luckValue = Model.GameSetBaseMap.get(34).intArray[1] * multiple;
        }
        vo.setGangUserData(GangUserDataEnum.DIVINATION_COUNT,vo.getGangUserData(GangUserDataEnum.DIVINATION_COUNT)-1,true);
        vo.addGangUserData(GangUserDataEnum.CONTRIBUTION,Model.GameSetBaseMap.get(34).intArray[0],true);
        vo.luckValue += luckValue;
        AllSql.gangMemberSql.update(vo,AllSql.gangMemberSql.FIELD_LUCK_VALUE,vo.luckValue);
        user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
        user.activationModel.progressBuyAct(MissionConditionEnum.GANG_DIVINATION,0);
        new DivinationRspd(client,vo.luckValue,multiple);
    }
}
