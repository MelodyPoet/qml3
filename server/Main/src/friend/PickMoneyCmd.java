package friend;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.PickMoneyRqst;
import protocol.PickMoneyRspd;
import protocol.ServerTipRspd;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 2017/4/27.
 */
public class PickMoneyCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        PickMoneyRqst rqst = (PickMoneyRqst)baseRqst;
        byte count = user.friendModel.canPickCount;
        if(count <= 0)return;
        HashSet<Long> pickUser = user.friendModel.pickUser;
        if(rqst.guid == 0){
            Set<Long> myAttention = FriendModel.getMyAttention(user.guid);
            for(long userID : myAttention){
                if(pickUser.contains(userID))continue;
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
                if(cacheUserVo == null)continue;
                if(cacheUserVo.friendCacheModel.canPickedCount <= 0)continue;
                cacheUserVo.friendCacheModel.canPickedCount--;
                pickUser.add(userID);
                count--;
                user.activationModel.progressBuyAct(MissionConditionEnum.PICK_MONEY,0);
                if(count <= 0)break;
            }
        }else{
            if(pickUser.contains(rqst.guid))return;
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.guid);
            if(cacheUserVo == null)return;
            if(cacheUserVo.friendCacheModel.canPickedCount <= 0){
                new ServerTipRspd(client,(short)275,null);
                return;
            }
            cacheUserVo.friendCacheModel.canPickedCount--;
            pickUser.add(rqst.guid);
            count--;
            user.activationModel.progressBuyAct(MissionConditionEnum.PICK_MONEY,0);
        }
        int times = user.friendModel.canPickCount - count;
        user.friendModel.canPickCount = count;
        user.addUserData(UserDataEnum.MONEY, Model.GameSetBaseMap.get(42).intArray[1]*times,true, ReasonTypeEnum.PICK_MONEY);
        new PickMoneyRspd(client,rqst.guid,count);
        user.friendModel.saveSqlData();
    }
}
