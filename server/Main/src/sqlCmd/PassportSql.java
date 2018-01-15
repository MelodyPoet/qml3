package sqlCmd;

import sendMsg.ClosedTestModel;
import comm.CachePassportVo;
import comm.Model;
import comm.SqlPool;
import table.RobotBaseVo;
import table.RobotType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PassportSql extends BaseSql {
    public final String FIELD_GUID = "guid";
     public final String FIELD_DEVID = "devID";
    public final String FIELD_SERVER_ID = "serverID";
    public final String FIELD_PORTRAIT = "portrait";
    public final String FIELD_VIP = "vip";
    public final String FIELD_DIAMOND = "diamond";
    public final String FIELD_TELPHONE = "telphone";
    public final String FIELD_IDENTIFY_CODE = "identifyCode";
    public final String FIELD_CODE_DEAD_TIME = "codeDeadTime";
    public final String FIELD_IS_OLD_USER = "isOldUser";
    public final String FIELD_LOGIN_TIME = "loginTime";
    public final String FIELD_RMB = "rmb";
    public final String FIELD_GET_VIP_AWARD = "getVipAward";
    public final String FIELD_LAST_ROLE_ID = "lastRoleID";

    public PassportSql() {
        super("passport");
        initCachePassport();
    }

    public void isInitDataBase(){
        initDataBase();
        initCachePassport();
    }

    public void initDataBase(){
        Connection conn = SqlPool.getConn();
        String cmd;
        try {
            ArrayList<RobotBaseVo> robotList = Model.RobotBaseMap.get((int) RobotType.ARENA);
            int i =1;
            for(RobotBaseVo robot : robotList){
                long guid=AllSql.passportSql.getNewGuid();
                cmd = "insert into passport("+FIELD_GUID+","+FIELD_DEVID+","+FIELD_SERVER_ID+") " +
                        "values("+guid+ ",\'"+ i++ +"\',"+Model.serverID+")";
                Statement state = conn.createStatement();
                state.executeUpdate(cmd);
            }
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
        } finally {
            SqlPool.release(conn);
        }
    }

    public boolean initCachePassport() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            conn.setAutoCommit(true);
            cmd = "select guid,devID,vip,telphone,identifyCode,codeDeadTime,isOldUser,loginTime,rmb,getVipAward,diamond,serverID,lastRoleID  from  passport";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                CachePassportVo cpvo = new CachePassportVo();

                cpvo.guid = dataSet.getLong(1);
                //cpvo.devID = dataSet.getString(2);
               // cpvo.serverID = (short)dataSet.getInt(12);
                cpvo.vip = dataSet.getByte(3);
                cpvo.telphone = dataSet.getString(4);
                cpvo.identifyCode = dataSet.getString(5);
                cpvo.codeDeadTime = dataSet.getInt(6);
                cpvo.isOldUser = dataSet.getByte(7);
                cpvo.loginTime = dataSet.getShort(8);
                cpvo.rmb = dataSet.getInt(9);
                cpvo.getVipAward = dataSet.getString(10);
                if(cpvo.getVipAward == null)cpvo.getVipAward ="";
                cpvo.diamond = dataSet.getInt(11);
                cpvo.lastRoleID = dataSet.getByte(13);

                CachePassportVo.guidMap.put(cpvo.guid,cpvo);
               // CachePassportVo.devMap.put(cpvo.serverID+"_"+cpvo.devID,cpvo);
                if(!"".equals(cpvo.telphone) && ClosedTestModel.IDENTIFY_SUCCESS.equals(cpvo.identifyCode)){
                    CachePassportVo.useTelphone.add(cpvo.telphone);
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




 	public void update(CachePassportVo passportVo,String keyName,int value) {
 		  update(keyName, value+"", passportVo.guid);
	}
    public void update(CachePassportVo passportVo,String keyName,String value) {
        update(keyName, value, passportVo.guid);
    }



}
