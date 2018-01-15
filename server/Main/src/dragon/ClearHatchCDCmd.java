package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ClearHatchCDRqst;
import protocol.GeneralSuccessRspd;
import table.CommCooldownBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2016/11/19.
 */
public class ClearHatchCDCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ClearHatchCDRqst rqst = (ClearHatchCDRqst)baseRqst;
        clearHatchCD(client,user,rqst.tempID);
    }

    private void clearHatchCD(Client client,User user,byte tempID){
        if(!user.cacheUserVo.dragonEggModel.touchMap.containsKey(tempID))return;
        CommCooldownBaseVo cooldownBaseVo= Model.CommCooldownBaseMap.get(10001);
        if(cooldownBaseVo == null || cooldownBaseVo.clearCost<=0)return;
        DragonEggVo dragonEggVo = user.cacheUserVo.dragonEggModel.getEggByTempID(tempID);
        if(dragonEggVo == null)return;
        int time = JkTools.getGameServerTime(client) - dragonEggVo.time;
        if(time<=0){
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, -time / cooldownBaseVo.clearCost + 1, true, ReasonTypeEnum.HATCH_CLEAR_CD) == false)
                return;
        }
        dragonEggVo.time = 0;
        user.cacheUserVo.dragonEggModel.saveSqlData();
        new GeneralSuccessRspd(client,ClearHatchCDRqst.PRO_ID);
    }
}
