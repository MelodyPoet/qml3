package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.LotteryBaseVo;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/8/2.
 */
public class LotteryCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        LotteryRqst rqst = (LotteryRqst)baseRqst;
        ArrayList<LotteryBaseVo> list = Model.LotteryBaseMap.get((int)rqst.id);
        if(list == null)return;
        LotteryBaseVo lotteryBaseVo = list.get(rqst.index);
        if(user.cacheUserVo.level < lotteryBaseVo.needLevel)return;
        short freeCost = (short) lotteryBaseVo.freeCost;
        boolean isCost = false;
        if(freeCost != 0){
            if(freeCost > 0){
                int count = user.getUserData(freeCost);
                if(count > 0){
                    user.setUserData(freeCost,count-1,true);
                    isCost = true;
                }
            }else{
                freeCost = (short)-freeCost;
                int now = JkTools.getGameServerTime(client);
                int time = now - user.getUserData(freeCost);
                if(time >= 0){
                    user.setUserData(freeCost,now + lotteryBaseVo.freeTime*60,true);
                    isCost = true;
                }
            }
        }
        if(!isCost){
            if(lotteryBaseVo.costUserData != null){
                if(user.enoughForcostUserDataAndProp(lotteryBaseVo.costUserData) == true){
                    int[] cost = {lotteryBaseVo.costData[0],lotteryBaseVo.costData[1]*lotteryBaseVo.dailyPercent/100};
                    if(user.costUserDataAndPropList(cost,true, ReasonTypeEnum.LOTTERY,null) == true){
                        user.costUserDataAndPropList(lotteryBaseVo.costUserData,true,ReasonTypeEnum.LOTTERY,null);
                        isCost = true;
                    }
                }
            }
        }
        if(!isCost){
            int percent = 100;
            if(lotteryBaseVo.percent > 0)percent = lotteryBaseVo.percent;
            int[] cost = {lotteryBaseVo.costData[0],lotteryBaseVo.costData[1]*percent/100};
            if(user.costUserDataAndPropList(cost,true,ReasonTypeEnum.LOTTERY,null) == false)return;
        }
        user.addUserData(UserDataEnum.LJ_LOTTERY_COUNT,lotteryBaseVo.count,true);
        user.activationModel.progressBuyAct(MissionConditionEnum.LOTTERY,0);
        ArrayList<PropPVo> dropList = BaseModel.getDropProps(lotteryBaseVo.dropGroup,user.baseID);
        user.propModel.addListToBag(dropList,ReasonTypeEnum.LOTTERY);
        new AwardShowRspd(client,dropList);
        new LotteryRspd(client,rqst.id,rqst.index);
    }
}
