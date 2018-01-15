package friend;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GiveTiliRqst;
import protocol.GiveTiliRspd;
import table.MissionConditionEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 2017/4/27.
 */
public class GiveTiliCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GiveTiliRqst rqst = (GiveTiliRqst) baseRqst;
        HashSet<Long> giveUser = user.friendModel.giveUser;
        if(rqst.guid == 0){
            Set<Long> myAttention = FriendModel.getMyAttention(user.guid);
            for(long userID : myAttention){
                if(giveUser.contains(userID))continue;
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
                if(cacheUserVo == null)continue;
                giveUser.add(userID);
                FriendGiftVo.createFriendGift(user.guid,cacheUserVo);
                user.activationModel.progressBuyAct(MissionConditionEnum.GIVE_TILI,0);
            }
        }else{
            if(giveUser.contains(rqst.guid))return;
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.guid);
            if(cacheUserVo == null)return;
            giveUser.add(rqst.guid);
            FriendGiftVo.createFriendGift(user.guid,cacheUserVo);
            user.activationModel.progressBuyAct(MissionConditionEnum.GIVE_TILI,0);
        }
        new GiveTiliRspd(client,rqst.guid);
        user.friendModel.saveSqlData();
    }
}
