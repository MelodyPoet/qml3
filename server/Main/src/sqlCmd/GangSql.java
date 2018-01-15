package sqlCmd;

import comm.SqlPool;
import gang.Gang;
import gang.GangVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class GangSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_NAME = "name";
    public final String FIELD_LEVEL = "level";
    public final String FIELD_EXP = "exp";
    public final String FIELD_NOTICE = "notice";
	public final String FIELD_ZDL = "zdl";
    public final String FIELD_BUILD = "build";
    public final String FIELD_MAX_USER_COUNT = "maxUserCount";
    public final String FIELD_LAST_RESET_DAY = "lastResetDay";
    public final String FIELD_ZDL_LIMIT = "zdlLimit";
    public final String FIELD_APPLY_LIST = "applyList";
    public final String FIELD_GANG_LOG = "gangLog";
    public final String FIELD_PORTRAIT = "portrait";
    public final String FIELD_GIFT_LOG = "giftLog";
    public final String FIELD_GIFT_LIST = "giftList";
    public final String FIELD_GANG_MAP = "gangMap";

    public GangSql() {
        super("gang");
        initCacheGang();
    }

//    public boolean initGuid() {
//        currentGuid = 0;
//        return super.initGuid();
//    }

    public boolean initCacheGang(){
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try{
            cmd = "select guid,name,level,exp,notice,zdl,build,maxUserCount,lastResetDay,zdlLimit,applyList,gangLog,portrait,giftLog,giftList,gangMap from  gang";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                GangVo vo = new GangVo();
                vo.gangID = dataSet.getLong(index++);
                vo.gangName = dataSet.getString(index++);
                vo.level = dataSet.getByte(index++);
                vo.exp = dataSet.getInt(index++);
                vo.notice = dataSet.getString(index++);
				vo.zdl = dataSet.getInt(index++);
                vo.loadGangBuildInfo(dataSet.getString(index++));
                vo.maxUserCount = dataSet.getInt(index++);
                vo.lastResetDay = dataSet.getInt(index++);
                vo.zdlLimit = dataSet.getInt(index++);
                vo.loadGangApplyInfo(dataSet.getString(index++));
                vo.loadGangLog(dataSet.getString(index++));
                vo.portrait = dataSet.getByte(index++);
                vo.loadGangGiftLog(dataSet.getString(index++));
                vo.loadGangGiftList(dataSet.getString(index++));
                vo.loadGangMap(dataSet.getString(index++));
                Gang.addOne(vo);
            }
            Gang.sort();
            dataSet.close();
        }catch (Exception e){
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean insertNew(GangVo gangVo) {

        long guid= AllSql.gangSql.getNewGuid();
        insert(FIELD_GUID+","+FIELD_NAME+","+FIELD_LEVEL+","+FIELD_ZDL+","+FIELD_MAX_USER_COUNT+","+FIELD_PORTRAIT,guid+ ",\'"+ gangVo.gangName +"\',"+gangVo.level+","+gangVo.zdl+","+gangVo.maxUserCount+","+gangVo.portrait);
        gangVo.gangID = guid;
        return true;
    }

    public void update(GangVo gangVo,String keyName,int value) {
        update(keyName, value+"", gangVo.gangID);
    }
    public void update(GangVo gangVo,String keyName,String value) {
        update(keyName, value, gangVo.gangID);
    }

}
