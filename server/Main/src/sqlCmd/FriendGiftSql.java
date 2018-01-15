package sqlCmd;

import comm.CacheUserVo;
import comm.SqlPool;
import comm.User;
import friend.FriendGiftVo;
import friend.FriendModel;
import gluffy.utils.JkTools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2017/4/28.
 */
public class FriendGiftSql extends BaseSql{

    public final String FIELD_GUID = "guid";
    public final String FIELD_GIVER_ID = "giverID";
    public final String FIELD_RECEIVER_ID = "receiverID";
    public final String FIELD_CREATE_TIME = "createTime";
    public final String FIELD_IS_GET = "isGet";


    public FriendGiftSql() {
        super("friend_gift");
    }

    public boolean loadOneFriendGift(User user) {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            int time = JkTools.getGameServerTime(null);
            cmd = "select guid,giverID,receiverID,createTime,isGet from  friend_gift where receiverID = " + user.guid + " and createTime > "+time+" order by createTime desc";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            FriendModel friendModel = user.friendModel;
            while (dataSet.next()) {
                int index = 1;
                FriendGiftVo vo = new FriendGiftVo();
                vo.guid = dataSet.getLong(index++);
                vo.giverID = dataSet.getLong(index++);
                vo.receiverID = dataSet.getLong(index++);
                vo.createTime = dataSet.getInt(index++);
                vo.isGet = dataSet.getByte(index++);
                friendModel.giftList.add(vo);
            }
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(user.guid);
            if(cacheUserVo != null){
                cacheUserVo.friendCacheModel.addGiftList.clear();
            }
            dataSet.close();
        } catch (Exception e) {
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean insertNew(FriendGiftVo friendGiftVo) {

        long guid = AllSql.gangSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_GIVER_ID + "," + FIELD_RECEIVER_ID + "," + FIELD_CREATE_TIME, guid + "," + friendGiftVo.giverID + "," + friendGiftVo.receiverID + "," + friendGiftVo.createTime);
        friendGiftVo.guid = guid;
        return true;
    }

    public void update(FriendGiftVo friendGiftVo, String keyName, int value) {
        update(keyName, value + "", friendGiftVo.guid);
    }

    public void update(FriendGiftVo friendGiftVo, String keyName, String value) {
        update(keyName, value, friendGiftVo.guid);
    }
}
