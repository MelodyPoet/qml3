package sqlCmd;

import comm.CacheUserVo;
import comm.Model;
import comm.SqlPool;
import mine.MineLootVo;
import mine.MineModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class MineLootSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_USERID = "userID";
    public final String FIELD_LOST_MINE = "lostMine";
    public final String FIELD_LOST_STONE = "lostStone";
    public final String FIELD_CREATE_TIME = "createTime";


    public MineLootSql() {
        super("mine_loot");
        initMineLoot();
    }

    public boolean initMineLoot() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select * from  mine_loot";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                MineLootVo mineLootVo = new MineLootVo();
                mineLootVo.guid = dataSet.getLong(index++);
                mineLootVo.userID = dataSet.getLong(index++);
                mineLootVo.lostMine = dataSet.getInt(index++);
                mineLootVo.lostStone = dataSet.getInt(index++);
                mineLootVo.createTime = dataSet.getInt(index++);
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(mineLootVo.userID);
                MineModel mineModel = cacheUserVo.mineModel;
                mineModel.lootset.add(mineLootVo.guid);
                if(mineLootVo.createTime < mineModel.mineStartTime && mineLootVo.createTime > mineModel.mineEndTime)continue;
                mineModel.lostMineCount += mineLootVo.lostMine;
                mineModel.lostStoneCount += mineLootVo.lostStone;
                if(mineLootVo.createTime > mineModel.lastLootedTime)continue;
                mineModel.lastLootedTime = mineLootVo.createTime;
                mineModel.protectTime = mineModel.lastLootedTime + Model.GameSetBaseMap.get(65).intValue;
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

    public boolean insertNew(MineLootVo mineLootVo) {

        long guid = AllSql.mineLootSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_USERID + "," + FIELD_LOST_MINE + "," + FIELD_LOST_STONE + "," + FIELD_CREATE_TIME, guid + "," + mineLootVo.userID + "," + mineLootVo.lostMine + "," + mineLootVo.lostStone+ "," + mineLootVo.createTime);
        mineLootVo.guid = guid;
        return true;
    }
}
