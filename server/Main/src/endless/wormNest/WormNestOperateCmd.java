package endless.wormNest;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.WormNestOperateRqst;
import table.CommCooldownBaseVo;
import table.ReasonTypeEnum;
import table.TowerBaseVo;
import table.UserDataEnum;
import protocol.WormNestOperateRspd;

import java.util.ArrayList;

/**
 * Created by admin on 2016/7/9.
 */
public class WormNestOperateCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WormNestOperateRqst rqst = (WormNestOperateRqst) baseRqst;
        wormNestOperate(client, user, rqst.type);
    }

    private void wormNestOperate(Client client, User user, byte type) {
        if (type == -1) {
            if(user.getUserData(UserDataEnum.WormNestResetTime)<=0)return;
            user.setUserData(UserDataEnum.WormNestCurrentFloor, 1, true);
            user.setUserData(UserDataEnum.WormNestResetTime,user.getUserData(UserDataEnum.WormNestResetTime)-1,true);
        }
        if (type == 1) {
            int cd = 0;
            ArrayList<TowerBaseVo> towerBaseVoList = Model.TowerBaseMap.get(1);
            for (int i = user.getUserData(UserDataEnum.WormNestCurrentFloor); i <= user.getUserData(UserDataEnum.WormNestMaxFloor); i++) {
                TowerBaseVo towerBaseVo = towerBaseVoList.get(i-1);
                if (towerBaseVo == null) {
                    return;
                }
                cd += towerBaseVo.cd;
            }
            user.setUserData(UserDataEnum.WormNestNextEndTime, JkTools.getGameServerTime(client) + cd, true);
        }
        if(type == 2){
            int cd = 0;
            ArrayList<TowerBaseVo> towerBaseVoList = Model.TowerBaseMap.get(1);
            for (int i = user.getUserData(UserDataEnum.WormNestCurrentFloor); i <= user.getUserData(UserDataEnum.WormNestMaxFloor); i++) {
                TowerBaseVo towerBaseVo = towerBaseVoList.get(i-1);
                if (towerBaseVo == null) {
                    return;
                }
                cd += towerBaseVo.cd;
            }

            CommCooldownBaseVo cooldownBaseVo= Model.CommCooldownBaseMap.get((int)UserDataEnum.WormNestNextEndTime);
            if(cooldownBaseVo.clearCost<=0)return;
            if(cd>=0){
                if (user.costUserDataAndProp(UserDataEnum.DIAMOND, cd / cooldownBaseVo.clearCost + 1, true, ReasonTypeEnum.WORM_NEST_OPERATE) == false)
                    return;
            }
            user.setUserData(UserDataEnum.WormNestNextEndTime,-1,true);

        }
        new WormNestOperateRspd(client,type);
    }
}
