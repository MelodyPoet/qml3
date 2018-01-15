package friend;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.AttentionListRqst;
import protocol.AttentionListRspd;
import protocol.FriendUserPVo;
import protocol.ServerTipRspd;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 2017/4/5.
 */
public class AttentionListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        AttentionListRqst rqst = (AttentionListRqst)baseRqst;
        ArrayList<FriendUserPVo> users = new ArrayList<>();
        int startIndex = rqst.startIndex;
        if (startIndex < 0) startIndex = 0;
        int total = 0;
        byte type = rqst.type;
        switch(type){
            case 1:
                Set<Long> myAttention = FriendModel.getMyAttention(user.guid);
                getList(user,myAttention,users,startIndex);
                total = myAttention.size();
                break;
            case 2:
                Set<Long> attentionMe = FriendModel.getAttentionMe(user.guid);
                getList(user,attentionMe,users,startIndex);
                total = attentionMe.size();
                if(user.friendModel.lastAttentionCount != total){
                    user.friendModel.lastAttentionCount = total;
                    user.friendModel.saveSqlData();
                }
                break;
            case 3:
                if(user.friendModel.recommendList.size() < Model.GameSetBaseMap.get(31).intArray[3]){
                   user.friendModel.getRecommend(false);
                }
                getList(user,user.friendModel.recommendList,users,startIndex);
                break;
            case 4:
                if("".equals(rqst.name))break;
                for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
                    if(cacheUserVo.guid == user.guid)continue;
                    if(cacheUserVo.name.indexOf(rqst.name) < 0)continue;
                    users.add(UserVoAdapter.toFriendUserPVo(cacheUserVo,user.cacheUserVo));
                }
                break;
            case 7:
                user.friendModel.getRecommend(true);
                getList(user,user.friendModel.recommendList,users,startIndex);
                type = 3;
                new ServerTipRspd(client,(short)324,null);
                break;
        }
        new AttentionListRspd(client,startIndex,type,users,total);
    }

    private void getList(User user,Set<Long> set,ArrayList<FriendUserPVo> users,int startIndex){
        if (startIndex > set.size())return;
        int i=0;
        HashSet<Long> pickUser = user.friendModel.pickUser;
        HashSet<Long> giveUser =user.friendModel.giveUser;
        for(long guid : set){
            if(i++<startIndex)continue;
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(guid);
            if(cacheUserVo == null)continue;
            FriendUserPVo friendUserPVo = UserVoAdapter.toFriendUserPVo(cacheUserVo,user.cacheUserVo);
            users.add(friendUserPVo);
            friendUserPVo.isCanPick = pickUser.contains(friendUserPVo.guid) || user.friendModel.canPickCount <=0 || cacheUserVo.friendCacheModel.canPickedCount<=0 ?(byte) 0:(byte) 1;
            friendUserPVo.isGive = giveUser.contains(friendUserPVo.guid)?(byte) 1:(byte) 0;
            if(users.size() >= 10)return;
        }
    }
}
