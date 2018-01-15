package sqlCmd;

import comm.Model;
import comm.SqlPool;
import comm.User;
import gluffy.utils.JkTools;
import heroTag.HeroTagModel;
import heroTag.HeroTagVo;
import table.HeroTagBaseVo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by admin on 2016/6/30.
 */
public class HeroTagSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_USER_ID = "userID";
    public final String FIELD_TAG_ID = "tagID";
    public final String FIELD_STATUS = "status";
    public final String FIELD_DEAD_TIME = "deadTime";


    public HeroTagSql() {
        super("hero_tag");
    }

    public boolean loadOneHeroTag(User user) {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            cmd = "select * from  hero_tag where userID = " + user.guid + " and (deadTime > " + JkTools.getGameServerTime(null) + " or deadTime = 0)";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            HeroTagModel heroTagModel = user.heroTagModel;
            while (dataSet.next()) {
                int index = 1;
                HeroTagVo vo = new HeroTagVo();
                vo.guid = dataSet.getLong(index++);
                vo.userID = dataSet.getLong(index++);
                vo.tagID = dataSet.getShort(index++);
                vo.status = dataSet.getByte(index++);
                vo.deadTime = dataSet.getInt(index++);
                HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get((int)vo.tagID);
                HashMap<Short,HeroTagVo> map = null;
                if(heroTagBaseVo.type == 1){
                    map = heroTagModel.zdlTagMap;
                }else{
                    map = heroTagModel.activeTagMap;
                }
                HeroTagVo heroTagVo = map.get(vo.tagID);
                if(heroTagVo != null && vo.deadTime != 0 && vo.deadTime <= heroTagVo.deadTime)continue;
                map.put(vo.tagID,vo);
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

    public boolean insertNew(HeroTagVo heroTagVo) {

        long guid = AllSql.heroTagSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_USER_ID + "," + FIELD_TAG_ID + "," + FIELD_STATUS + "," + FIELD_DEAD_TIME, guid + "," + heroTagVo.userID + "," + heroTagVo.tagID + "," + heroTagVo.status+ "," + heroTagVo.deadTime);
        heroTagVo.guid = guid;
        return true;
    }

    public void update(HeroTagVo heroTagVo, String keyName, int value) {
        update(keyName, value + "", heroTagVo.guid);
    }

    public void update(HeroTagVo heroTagVo, String keyName, String value) {
        update(keyName, value, heroTagVo.guid);
    }

}
