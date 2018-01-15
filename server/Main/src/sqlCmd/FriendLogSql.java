package sqlCmd;

import comm.SqlPool;
import comm.User;
import friend.FriendModel;
import friend.FriendSysInfoVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

/**
 * Created by admin on 2016/6/30.
 */
public class FriendLogSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_TYPE = "type";
    public final String FIELD_ATTENTIONID = "attentionID";
    public final String FIELD_ATTENTIONEDID = "attentionedID";
    public final String FIELD_UNAME = "uname";
    public final String FIELD_CREATETIME = "createTime";


    public FriendLogSql() {
        super("friend_log");
    }

    public boolean loadOneFriendLog(User user) {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select guid,type,attentionID,attentionedID,uname,createTime from  friend_log where attentionedID = " + user.guid + " order by createTime desc";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            FriendModel friendModel = user.friendModel;
            HashSet<Long> set = new HashSet<>();
            HashSet<Long> del = new HashSet<>();
            while (dataSet.next()) {
                int index = 1;
                FriendSysInfoVo vo = new FriendSysInfoVo();
                vo.guid = dataSet.getLong(index++);
                vo.type = dataSet.getByte(index++);
                vo.attentionID = dataSet.getLong(index++);
                vo.attentionedID = dataSet.getLong(index++);
                vo.uname = dataSet.getString(index++);
                vo.createTime = dataSet.getInt(index++);
                if(set.contains(vo.attentionID)){
                    del.add(vo.guid);
                    continue;
                }
                set.add(vo.attentionID);
                friendModel.sysInfoList.add(vo);
                if(vo.guid > friendModel.lastSysInfoID)user.cacheUserVo.newSysInfoCount++;
            }
            for(long guid : del){
                delete(guid);
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

    public boolean insertNew(FriendSysInfoVo friendSysInfoVo) {

        long guid = AllSql.gangSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_TYPE + "," + FIELD_ATTENTIONID + "," + FIELD_ATTENTIONEDID + "," + FIELD_UNAME + "," + FIELD_CREATETIME, guid + "," + friendSysInfoVo.type + "," + friendSysInfoVo.attentionID + "," + friendSysInfoVo.attentionedID+ ",\'" + friendSysInfoVo.uname+ "\'," + friendSysInfoVo.createTime);
        friendSysInfoVo.guid = guid;
        return true;
    }

    public void update(FriendSysInfoVo friendSysInfoVo, String keyName, int value) {
        update(keyName, value + "", friendSysInfoVo.guid);
    }

    public void update(FriendSysInfoVo friendSysInfoVo, String keyName, String value) {
        update(keyName, value, friendSysInfoVo.guid);
    }

}
