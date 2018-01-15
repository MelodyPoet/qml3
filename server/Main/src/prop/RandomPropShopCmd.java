package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.PropShopPVo;
import protocol.RandomPropShopRqst;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2016/8/3.
 */
public class RandomPropShopCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RandomPropShopRqst rqst = (RandomPropShopRqst) baseRqst;
        randomPropShop(client, user, (int)rqst.type);
    }

    private void randomPropShop(Client client,User user,int type){
        if(type == 0){
            int cdEndTime = user.propModel.propShopFlushCD;
            int time = JkTools.getGameServerTime(client) - cdEndTime;
            System.out.println("time============================="+time);
            if(time >=0){
                user.propModel.propShopFlushCD = PropModel.nextEndTime(client);
                user.propModel.randomPropShop(0,true);
            }
            return;
        }
        PropShopBaseVo vo = Model.PropShopBaseMap.get(type).get(0);
        if(vo.type != 0){
            PropShopPVo propShopPVo = user.propModel.propShopMap.get((byte)type);
            if(propShopPVo == null)return;
            if(propShopPVo.freeTimes<=0){
                VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)user.cacheUserVo.passportVo.vip);
                if(vipBaseVo == null)return;
                if(propShopPVo.flushCount >= vipBaseVo.shopFlushCount)return;
                ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int)ModuleUIEnum.PROP_SHOP);
                CommCostBaseVo costBaseVo = costList.get(propShopPVo.flushCount);
                if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.FLUSH_PROP_SHOP,null) == false)return;
                propShopPVo.flushCount++;
            }else{
                propShopPVo.freeTimes--;
            }
            user.propModel.propShopFlushCD = PropModel.nextEndTime(client);
            user.addUserData(UserDataEnum.LJ_SHOP_FLUSH_COUNT,1,true);
        }
        user.propModel.randomPropShop(type,true);
    }

}
