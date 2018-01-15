package friend;

import comm.CacheUserVo;
import comm.Model;
import gluffy.utils.JkTools;
import sqlCmd.AllSql;

/**
 * Created by admin on 2017/4/28.
 */
public class FriendGiftVo {
    public long guid;
    public long giverID;
    public long receiverID;
    public int createTime;
    public byte isGet;

    public static void createFriendGift(long giverID, CacheUserVo receiver){
        FriendGiftVo friendGiftVo = new FriendGiftVo();
        friendGiftVo.giverID = giverID;
        friendGiftVo.receiverID = receiver.guid;
        friendGiftVo.createTime = JkTools.getGameServerTime(null) + 3* Model.ONE_DAY_TIME;
        AllSql.friendGiftSql.insertNew(friendGiftVo);
        receiver.friendCacheModel.addGiftList.add(friendGiftVo);
    }
}

