package sqlCmd;

import comm.SqlPool;
import gameset.GameSetModel;
import gameset.GameSetVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class GameSetSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_ARENA = "arena";
    public final String FIELD_REDNESS = "redness";
    public final String FIELD_ACTIVE = "active";
    public final String FIELD_MINE = "mine";

    public GameSetSql() {
        super("gameset");
        initCacheGameSet();
    }

    public boolean initCacheGameSet() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select * from  gameset limit 1";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            if (dataSet.next()==true) {
                int index = 1;
                GameSetVo vo = new GameSetVo();
                vo.guid = dataSet.getLong(index++);
                vo.loadRedness(dataSet.getString(index++));
                vo.loadActive(dataSet.getString(index++));
                vo.loadMine(dataSet.getString(index++));
                GameSetModel.gameSetVo = vo;
            }else{
                cmd = "INSERT INTO  gameset (guid) VALUES(1)";
                state.executeUpdate(cmd);
                GameSetVo vo = new GameSetVo();
                vo.guid = 1;
                GameSetModel.gameSetVo = vo;
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

    public void update(GameSetVo gameSetVo, String keyName, int value) {
        update(keyName, value + "", gameSetVo.guid);
    }

    public void update(GameSetVo gameSetVo, String keyName, String value) {
        update(keyName, value, gameSetVo.guid);
    }

}
