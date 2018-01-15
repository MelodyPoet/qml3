package sqlCmd;

import comm.CacheUserVo;
import comm.SqlPool;
import mine.MineRecruitVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class MineRecruitSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_USERID = "userID";
    public final String FIELD_INDEX = "uIndex";
    public final String FIELD_CREATE_TIME = "createTime";


    public MineRecruitSql() {
        super("mine_recruit");
        initMineRecruit();
    }

    public boolean initMineRecruit() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select * from  mine_recruit";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                MineRecruitVo mineRecruitVo = new MineRecruitVo();
                mineRecruitVo.guid = dataSet.getLong(index++);
                long userID = dataSet.getLong(index++);
                mineRecruitVo.index = dataSet.getByte(index++);
                mineRecruitVo.createTime = dataSet.getInt(index++);
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
                mineRecruitVo.cacheUserVo = cacheUserVo;
                if(mineRecruitVo.createTime <= cacheUserVo.mineModel.lastLootedTime)continue;
                cacheUserVo.mineModel.teamMap.put(mineRecruitVo.index,mineRecruitVo);
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

    public boolean insertNew(MineRecruitVo mineRecruitVo) {

        long guid = AllSql.mineRecruitSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_USERID + "," + FIELD_INDEX+ "," + FIELD_CREATE_TIME, guid + "," + mineRecruitVo.cacheUserVo.guid + "," + mineRecruitVo.index + "," + mineRecruitVo.createTime);
        mineRecruitVo.guid = guid;
        return true;
    }
}
