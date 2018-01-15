package base;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import org.jdom2.Element;
import protocol.LoginRqst;
import protocol.LoginRspd;
import protocol.PassportRoleUpdateRspd;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


public class LoginCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
LoginRqst rqst= (LoginRqst) baseRqst;
//        try{
//            if(rqst.app !="" || rqst.sdk!=""||rqst.uin !="" || rqst.sess!=""){
//                if(!YijieModel.checkUserLogin(rqst)){
//                    return;
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        if(rqst.clientVs!=Model.serverVs){
//            new ServerTipRspd(client,(short)10,null);
//            return;
//        }
//		if(client.userMap==null){
//			client.userMap=new HashMap<>();
//			AllSql.userSql.loadone(client);
//		}
		////账号密码验证

//验证内测码
        if(rqst.testCode==0){
            new ServerTipRspd(client,(short)997,null);
            return;
        }
//        Element rootEl = Model.configXmlDoc.getRootElement();
//
//        Connection conn = null;
//        ResultSet query = null;
//        try {
//            conn = DriverManager.getConnection(rootEl.getChild("sqlCmd").getValue(), rootEl.getChild("sqlUser").getValue(),rootEl.getChild("sqlPwd").getValue());
//            query = conn.createStatement().executeQuery("select * from test_code where code="+  rqst.testCode+" and open =1 limit 1");
//            if(query.next()==false){
//                new ServerTipRspd(client,(short)997,null);
//                return;
//            }
//            SqlPool.release(conn);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }




        
        Client.createPassport(client);

		for (CacheUserVo us : client.passportVo.userMap.values()) {


			new PassportRoleUpdateRspd(client,us.baseID, us.level,JkTools.asListTrimNull(us.equipItems),us.skin,us.wingModel.equipWingID,us.zdl,us.name,us.portrait);
		}
        client.passportVo.clientTempID++;
        if( client.passportVo.clientTempID==0) client.passportVo.clientTempID=1;
		new LoginRspd(client,client.passportVo.guid,(byte)0,JkTools.getGameServerTime(client),Model.openDay.get(Calendar.YEAR)*10000+Model.openDay.get(Calendar.MONTH)*100+Model.openDay.get(Calendar.DAY_OF_MONTH),
                client.passportVo.vip,client.passportVo.lastRoleID,client.passportVo.isOldUser,client.passportVo.diamond,client.passportVo.clientTempID);
        client.passportLogined=true;
        client.passportVo.loginTime++;


        AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_LOGIN_TIME,client.passportVo.loginTime);

	}

    public void executeAutoLoad(Client client) {

//        if(client.userMap==null){
//            client.userMap=new HashMap<>();
//            AllSql.userSql.loadone(client);
//        }
//        for (User us : client.userMap.values()) {
//            us.cacheUserVo= CacheUserVo.guidMap.get(us.guid);
//            us.cacheUserVo.passportVo=client.passportVo;
//         }
        Client.createPassport(client);
        client.passportLogined=true;


    }

}
