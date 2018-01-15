package base;


import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.UnlockBagCellRqst;
import table.*;

/**
 * Created by admin on 2016/6/24.
 */
public class UnlockBagCellCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        UnlockBagCellRqst rqst = (UnlockBagCellRqst) baseRqst;
        unlockBagCell(client,user);
    }

    private void unlockBagCell(Client client,User user){
        user.flushBagCellCD(user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT));
        int bagCDCellIndex = user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)+1;
        BagBaseVo bagBaseVo = Model.BagBaseMap.get(bagCDCellIndex);
        if(bagBaseVo==null){
            return;
        }
        if(user.getUserData(UserDataEnum.BAG_CELL_CD_COUNT)-user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)<1){
            return;
        }
        UserUpdateBaseVo extra = Model.UserUpdateBaseMap.get(13);
        if(extra==null){
            return;
        }
        if(user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)>=extra.max){
            return;
        }
        if(user.costUserDataAndProp(UserDataEnum.DIAMOND, bagBaseVo.costItems[1], true, ReasonTypeEnum.UNLOCK_BAG_CELL) == false){
            return;
        }
        user.addUserData(UserDataEnum.BAG_CELL_HAVE_COUNT,1,true);
        user.updateZDL();
        if(user.getUserData(UserDataEnum.BAG_REMOVE_CD_TIME) == -1){
            BagBaseVo bagBaseVoNext = Model.BagBaseMap.get(user.getUserData(UserDataEnum.BAG_CELL_CD_COUNT)+1);
            if(bagBaseVoNext==null){
                if(user.getUserData(UserDataEnum.BAG_CELL_CD_COUNT) == extra.max){
                    user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL, -1, true);
                    user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, -1, true);
                }
                return;
            }
            user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, JkTools.getGameServerTime(client),true);
            user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL,JkTools.getGameServerTime(client)+bagBaseVoNext.coolTime,true);
        }
    }
}
