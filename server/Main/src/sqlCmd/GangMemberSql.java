package sqlCmd;

import comm.CacheUserVo;
import comm.SqlPool;
import gang.Gang;
import gang.GangUserVo;
import gang.GangVo;
import table.GangOfficeEnum;
import table.GangUserDataEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by admin on 2016/6/30.
 */
public class GangMemberSql extends BaseSql{

    public final String FIELD_GUID = "guid";
    public final String FIELD_ROLEID = "roleID";
    public final String FIELD_GANGID = "gangID";
    public final String FIELD_OFFICE = "office";
    public final String FIELD_CONTRIBUTION = "contribution";
    public final String FIELD_CONTRIBUTIONTODAY = "contributionToday";
    public final String FIELD_CONTRIBUTE_TIME = "contributeTime";
    public final String FIELD_END_CD_TIME = "endCDTime";
    public final String FIELD_LIKE_TIME = "likeTime";
    public final String FIELD_LUCK_VALUE = "luckValue";
    public final String FIELD_IS_GET_GIFT = "isGetGift";
    public final String FIELD_DIVINATION_COUNT = "divinationCount";
    public final String FIELD_GET_BOX_ID = "getBoxID";
    public final String FIELD_GUESS_COUNT = "guessCount";
    public final String FIELD_DRAGON_JINPO = "dragonJinpo";
    public final String FIELD_MAP_COUNT = "mapCount";
    public GangMemberSql() {
        super("gangmember");
        initCacheGangMenber();
    }

    public boolean initCacheGangMenber() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select guid,roleID,gangID,office,contribution,contributionToday,contributeTime,endCDTime,likeTime,luckValue,isGetGift,divinationCount,guessCount,getBoxID,dragonJinpo,mapCount  from  gangmember";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                GangUserVo vo=new GangUserVo();
                vo.guid = dataSet.getLong(index++);
                vo.cacheUserVo = CacheUserVo.allMap.get(dataSet.getLong(index++));
                GangVo gangVo = Gang.allGangMap.get(dataSet.getLong(index++));

                vo.office = dataSet.getByte(index++);
                vo.userdata[GangUserDataEnum.CONTRIBUTION] = dataSet.getInt(index++);
                vo.contributionToday = dataSet.getInt(index++);
                vo.contributeTime = dataSet.getByte(index++);
                vo.endCDTime = dataSet.getInt(index++);
                vo.likeTime = dataSet.getByte(index++);
                vo.luckValue = dataSet.getInt(index++);
                vo.isGetGift = dataSet.getByte(index++);
                vo.userdata[GangUserDataEnum.DIVINATION_COUNT] = dataSet.getInt(index++);
                vo.userdata[GangUserDataEnum.GUESS_COUNT] = dataSet.getInt(index++);
                vo.loadGetBoxID(dataSet.getString(index++));
                vo.userdata[GangUserDataEnum.DRAGON_JINPO] = dataSet.getInt(index++);
                vo.userdata[GangUserDataEnum.MAP_COUNT] = dataSet.getInt(index++);
                gangVo.users.put(vo.cacheUserVo.guid, vo);
                if(vo.office== GangOfficeEnum.MASTER){
                    gangVo.master=vo;
                }
                vo.cacheUserVo.gang.gangVo=gangVo;
            }
            for(GangVo gangVo : Gang.allGangList){
                gangVo.initUserRankList();
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

    public boolean insertNew(GangUserVo gangUserVo, long gangID) {
        long guid= AllSql.gangMemberSql.getNewGuid();
        insert(FIELD_GUID+","+FIELD_ROLEID+","+FIELD_GANGID+","+FIELD_OFFICE+","+FIELD_CONTRIBUTE_TIME+","+FIELD_END_CD_TIME+","+FIELD_LIKE_TIME,guid+ ",\'"+gangUserVo.cacheUserVo.guid+"\'," + gangID + "," + gangUserVo.office + "," + gangUserVo.contributeTime + "," + gangUserVo.endCDTime + "," + gangUserVo.likeTime);
        gangUserVo.guid =guid;
        return true;
    }

    public void update(GangUserVo gangUserVo,String keyName,int value) {
        update(keyName, value+"", gangUserVo.guid);
    }
    public void update(GangUserVo gangUserVo,String keyName,String value) {
        update(keyName, value, gangUserVo.guid);
    }

}
