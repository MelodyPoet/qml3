package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.CommCostRqst;
import protocol.CommCostRspd;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2017/8/11.
 */
public class CommCostCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        CommCostRqst rqst = (CommCostRqst)baseRqst;
        int id = -1;
        short type = 0;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo == null)return;
        switch (rqst.type){
            case ModuleUIEnum.ARENA_TREASURE:
                UserUpdateBaseVo extraVo = Model.UserUpdateBaseMap.get(33);
                type = UserDataEnum.SNATCH_BUY_COUNT;
                id = extraVo.init - user.getUserData(type);
                break;
            case ModuleUIEnum.BUY_TILI:
                type = UserDataEnum.EXCHANGE_TILI_COUNT;
                id = vipBaseVo.buyTili - user.getUserData(type);
                break;
            case ModuleUIEnum.ARENA_PERSON:
                type = UserDataEnum.EXCHANGE_ARENA_COUNT;
                id = vipBaseVo.arenaBuyCount - user.getUserData(type);
                break;
        }
        if(id<0)return;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int) rqst.type);
        if(costList == null)return;
        CommCostBaseVo costBaseVo = costList.get(id);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.COMM_COST,null) == false)return;
        if(costBaseVo.extra != null){
            user.propModel.addListToBag(costBaseVo.extra,ReasonTypeEnum.COMM_COST);
        }
        user.setUserData(type,user.getUserData(type)-1,true);
        user.activationModel.progressBuyAct(MissionConditionEnum.COMM_COST,rqst.type);
        if(rqst.type == ModuleUIEnum.BUY_TILI){
            user.addUserData(UserDataEnum.LJ_BUY_TILI_COUNT,1,true);
        }
        new CommCostRspd(client,rqst.type);
    }
}
