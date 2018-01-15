package sqlCmd;

import comm.SqlPool;
import friend.FriendModel;
import friend.RelationVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class FriendSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_USER1 = "user1";
    public final String FIELD_USER2 = "user2";
    public final String FIELD_RELATION = "relation";


    public FriendSql() {
        super("friend");
        initCacheFriend();
    }

    public boolean initCacheFriend() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select guid,user1,user2,relation from  friend";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                RelationVo vo = new RelationVo();
                vo.guid = dataSet.getLong(index++);
                vo.user1 = dataSet.getLong(index++);
                vo.user2 = dataSet.getLong(index++);
                vo.relation = dataSet.getByte(index++);
                if (vo.relation == 1 || vo.relation == 3) {
                    FriendModel.addAttention(vo.user1,vo.user2,vo);
                }
                if (vo.relation == 2 || vo.relation == 3) {
                    FriendModel.addAttention(vo.user2,vo.user1,vo);
                }

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

    public boolean insertNew(RelationVo relationVo) {

        long guid = AllSql.gangSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_USER1 + "," + FIELD_USER2 + "," + FIELD_RELATION, guid + "," + relationVo.user1 + "," + relationVo.user2 + "," + relationVo.relation);
        relationVo.guid = guid;
        return true;
    }

    public void update(RelationVo relationVo, String keyName, int value) {
        update(keyName, value + "", relationVo.guid);
    }

    public void update(RelationVo relationVo, String keyName, String value) {
        update(keyName, value, relationVo.guid);
    }

}
