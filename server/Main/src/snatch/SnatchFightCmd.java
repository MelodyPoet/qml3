package snatch;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.SnatchFightRqst;
import protocol.SnatchFightRspd;
import table.*;


public class SnatchFightCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if (user.getUserData(UserDataEnum.LEVEL) < SnatchModel.needLvl) return;
        SnatchFightRqst rqst = (SnatchFightRqst) baseRqst;
        if (rqst.index >= user.snatchModel.targets.size()) return;
//        user.flushDataTime(UserDataEnum.SNATCH_FIGHT_COUNT, UserDataEnum.NEXTTIME_SNATCH, true);
//        if (user.costUserDataAndProp(UserDataEnum.SNATCH_FIGHT_COUNT, 1, true) == false) return;

        if (user.costUserDataAndPropList(Model.GameSetBaseMap.get(78).intArray,true, ReasonTypeEnum.SNATCH_FIGHT,null) == false) return;

        CacheUserVo target = user.snatchModel.targets.get((int) rqst.index);
        boolean isWin = false;
        boolean isSnatch = false;
        //isWin = user.zdl > target.zdl;

        if (target.guid == 0) {
            isWin = Math.random() * 100 < (target.level > user.cacheUserVo.level ? 50 : 80);
            isSnatch = Math.random() * 100 < (target.level > user.cacheUserVo.level ? 80 : 50);
        }else {
            isWin = Math.random() * 100 < (target.zdl > user.cacheUserVo.zdl ? 50 : 80);
            isSnatch = Math.random() * 100 < (target.zdl > user.cacheUserVo.zdl ? 80 : 50);
        }
//        if (isWin) {
//            if (target.level >= user.getUserData(UserDataEnum.LEVEL)) {
//                // 高概率
//                isSnatch = Math.random() * 100 < JkTools.getRandBetween(40, 50);
//            } else {
//                isSnatch = Math.random() * 100 < JkTools.getRandBetween(10, 30);
//            }
//        }
        if (Model.PropBaseMap.get(user.snatchModel.propBaseID).quality > 2) {
            user.setUserData(UserDataEnum.NEXTTIME_SNATCH_SAFE, 0, true);
        }
        if (target.snatch_safe_time > JkTools.getGameServerTime(null)) {
            isSnatch = false;
        }

        if (isWin && isSnatch) {
            user.propModel.addListToBag(new int[]{user.snatchModel.propBaseID, 1},ReasonTypeEnum.SNATCH_FIGHT);
        }
        if (isWin) {
            ArenaRewardBaseVo rewardBaseVo = Model.ArenaRewardBaseMap.get((int) user.getUserData(UserDataEnum.LEVEL));
            user.propModel.addListToBag(rewardBaseVo.snatchReward,ReasonTypeEnum.SNATCH_FIGHT);
        }

        user.addUserData(UserDataEnum.LJ_SNATCH_COUNT, 1, true);
        user.activationModel.progressBuyAct(MissionConditionEnum.SNATCH, 0);
        new SnatchFightRspd(client, isWin, isSnatch,target.baseID);
    }


}
