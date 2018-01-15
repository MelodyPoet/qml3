package sqlCmd;

import prop.PropLogVo;

/**
 * Created by admin on 2016/6/30.
 */
public class PropLogSql extends BaseSql {

    public final String FIELD_GUID = "guid";
    public final String FIELD_USERID = "userID";
    public final String FIELD_PROP_ID = "propID";
    public final String FIELD_COUNT = "count";
    public final String FIELD_REASON_TYPE = "reasonType";
    public final String FIELD_CREATE_TIME = "createTime";


    public PropLogSql() {
        super("prop_log");
    }

    public boolean insertNew(PropLogVo propLogVo) {
        long guid = AllSql.mineRecruitSql.getNewGuid();
        insert(FIELD_GUID + "," + FIELD_USERID + "," + FIELD_PROP_ID+ "," + FIELD_COUNT + "," + FIELD_REASON_TYPE+ "," + FIELD_CREATE_TIME, guid + "," + propLogVo.userID + "," + propLogVo.propID + "," + propLogVo.count + "," + propLogVo.rensonType + "," + propLogVo.createTime);
        propLogVo.guid = guid;
        return true;
    }
}
