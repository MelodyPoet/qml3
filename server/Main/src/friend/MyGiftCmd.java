package friend;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.FriendGiftPVo;
import protocol.MyGiftRqst;
import protocol.MyGiftRspd;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by admin on 2017/4/27.
 */
public class MyGiftCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MyGiftRqst rqst = (MyGiftRqst)baseRqst;
        int startIndex = rqst.startIndex;
        ArrayList<FriendGiftPVo> list = new ArrayList<>();
        ArrayList<FriendGiftVo> addGiftList = user.cacheUserVo.friendCacheModel.addGiftList;
        ArrayList<FriendGiftVo> giftList = user.friendModel.giftList;
        if(addGiftList.size() > 0){
            for(FriendGiftVo friendGiftVo : addGiftList){
                user.friendModel.giftList.add(0,friendGiftVo);
            }
            addGiftList.clear();
        }
        if (startIndex > giftList.size())return;
        int i=0;
        HashSet<Long> removeSet = new HashSet();
        for(FriendGiftVo friendGiftVo : giftList) {
            if (i < startIndex) continue;
            if(JkTools.getGameServerTime(client) - friendGiftVo.createTime > 0){
                removeSet.add(friendGiftVo.guid);
                continue;
            }
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(friendGiftVo.giverID);
            if(cacheUserVo == null)continue;
            list.add(UserVoAdapter.toFriendGiftPVo(friendGiftVo,cacheUserVo,user.friendModel.canReceiveCount));
            i++;
            if (list.size() >= 10) return;
        }
        for(long guid : removeSet){
            giftList.remove(guid);
        }
        new MyGiftRspd(client,startIndex,list,giftList.size());
    }
}
