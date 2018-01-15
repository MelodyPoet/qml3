import airing.LoopAiringModel;
import base.LoginCmd;
import base.LoginRoleCmd;
import base.SystemLoopModel;
import base.UserUpdateModel;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;
import gluffy.utils.CpuDebuger;
import gluffy.utils.LogManager;
import mail.LoopSendMailModel;
import mission.LimitTimeModel;
import mission.MissionModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import protocol.*;

import qmshared.QuickProtocal;
import qmshared.MQLogicServer;
import rank.RankModel;
import snatch.SnatchModel;
import sqlCmd.AllSql;
import sqlCmd.SqlSaver;
import talk.LoopSendTalkModel;


import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by jackie on 14-4-16.
 */

public class GameServer  {
    //private static ArrayBlockingQueue<byte[]> readBytes=new ArrayBlockingQueue<>(50);
    private static byte[] reloginBytes;
    static MQLogicServer mqServer;

    public static void main(String[] args) throws Exception {
        LogManager.init();
        CheckUtils.init();
        SAXBuilder builder = new SAXBuilder();
        CpuDebuger.init(0, 0);

        Document doc = builder.build(new File("config/config.xml"));

        Element rootEl = doc.getRootElement();
        long serverID = Long.parseLong(rootEl.getChild("serverID").getValue());
        Model.configXmlDoc = doc;
        SqlPool.init(rootEl.getChild("sqlCmd").getValue(), rootEl.getChild("sqlUser").getValue(), rootEl.getChild("sqlPwd").getValue(), 1);
        Connection conn = SqlPool.getConn();
        ResultSet query = conn.createStatement().executeQuery("select * from server_info where id=" + serverID + " limit 1");
        if (query.next() == false) {
            //log sql error
            return;
        }
        System.out.println(query.getString("sql_cmd"));
        System.out.println(query.getString("sql_user"));
        System.out.println(query.getString("sql_pwd"));

        Model.openDay = Calendar.getInstance();


        Model.openDay.setTime(query.getDate("openDay"));
        int port = query.getInt("ser_port");

        String sql_cmd = "jdbc:mysql://" + query.getString("sql_ip") + ":" + query.getString("sql_port") + "/" + query.getString("sql_name") + "?characterEncoding=utf-8";
        SqlPool.init(sql_cmd, query.getString("sql_user"), query.getString("sql_pwd"), 2);//Integer.parseInt(rootEl.getChild("sqlPool").getValue()));

        int merge_to = query.getInt("merge_to");
        Model.platformID = query.getByte("platform");
        Model.gameID = query.getByte("gameID");
        Model.serverID = query.getInt("serverID");
        query.close();

        if (merge_to != 0) {
            if (merge_to != Model.serverID) return;
            query = conn.createStatement().executeQuery("select id from server_info where merge_to=" + Model.serverID + " and platform=" + Model.platformID + " and gameID=" + Model.gameID);
            while (query.next()) {
                Model.mergeSet.add(query.getInt(1));
            }
            query.close();
        } else {
            Model.mergeSet.add(Model.serverID);
        }
        SqlPool.release(conn);
        if (Model.mergeSet.size() == 0) {
            //log sql error
            return;
        }
        CpuDebuger.print("sql_inited", 0);

        Model.init();
        CpuDebuger.print("sql_inited", 0);
        // DynamicUserGroup.instance.init(Model.gameSetBaseVo.roomParas[0],Model.gameSetBaseVo.roomParas[1],Model.gameSetBaseVo.roomParas[2]);
        RqstCmdModel.init();
        CpuDebuger.print("RqstCmdModel", 0);

        AllSql.init();
        CpuDebuger.print("AllSql", 0);

        RankModel.init();
        CpuDebuger.print("RankModel", 0);

        SnatchModel.init();
        CpuDebuger.print("SnatchModel", 0);

        MemoryRobot.initAll();
        CpuDebuger.print("MemoryRobot", 0);

        new SqlSaver().start();

        LoopSendMailModel.init();
        CpuDebuger.print("LoopSendMailModel", 0);

        LoopSendTalkModel.init();
        CpuDebuger.print("LoopSendTalkModel", 0);

        LoopAiringModel.init();
        CpuDebuger.print("LoopAiringModel", 0);

        MissionModel.initLimitTime();
        CpuDebuger.print("MissionModel", 0);

        SystemLoopModel.init();
        CpuDebuger.print("SystemLoopModel", 0);

        UserUpdateModel.modelInit();
        CpuDebuger.print("UserUpdateModel", 0);

        LimitTimeModel.init();
        CpuDebuger.print("LimitTimeModel", 0);

      //  new ClientOfflineServer().start();
        new SysLoop().start();
        CpuDebuger.print("SysLoop", 0);


//

      //  new UdpGameServer(port + 1).start();
        CpuDebuger.print("gameServerStart", 0);
        Model.serverOpen = true;
        //  server.join();

        new GameServer();

    }

    GameServer() {


    mqServer=new  MQLogicServer(qmshared.Model.MainPort, value->
    {
        try{
            onRecvGate(value);
        }catch (Exception e){
            Client client= new Client();
            client.guid = value.guid;
            new ServerTipRspd(client,(short)(992),"992");
            LogManager.printError(e);
        }
    });
    mqServer.start();
}


    protected void onRecvGate( QuickProtocal protocal) {
        System.out.println("rev:pid="+protocal.proID);
        Client client= null;
        long uid=protocal.guid;
        if(Model.serverOpen == false){
            client = new Client();
            client.guid = uid;
            new ServerTipRspd(client,(short)(998),"998");

            // Client.sendTempClient(response.getOutputStream());
            return;
        }
        CpuDebuger.init(1, 30);


         short proID= protocal.proID;

        // System.out.println("uid="+uid);
        User user=null;
        //0 表示gate发的协议
        if(uid==0) {
        }else {
            client = Client.getOne(uid);
            if (client == null) {
                //客户端不存在且不是登陆请求 强制重登陆(一般为服务器重启后 或玩家离线太久后)
                // client=Client.createPassport(uid);
                if (proID != LoginRqst.PRO_ID) {
                    //    client =new Client();
//                client.tempDev=uid;
                    //   new GetServerVsRspd(client,Model.serverVs);
                    //  new ReloginRspd(client);
                    //return;
                    //先关闭自动登陆功能

                    client = new Client();
                    client.guid = uid;
                    new LoginCmd().executeAutoLoad(client);
                    new LoginRoleCmd(true).execute(client, null, null);
                    CpuDebuger.print("relogin", 1);
                } else {
                    client = new Client();
                    client.guid = uid;
                }
                if (client == null) return;

            }
//        if(proID!= LoginRqst.PRO_ID&&clientTempID!=client.passportVo.clientTempID) {
//
//            new ServerTipRspd(null,(short)(client.passportVo.clientTempID==0?15:996),null);
//           // Client.sendTempClient(response.getOutputStream());
//            return;
//        }
              user = client.currentUser;
            if (user == null) {
                if (proID != LoginRqst.PRO_ID && proID != RoleCreateRqst.PRO_ID && proID != LoginRoleRqst.PRO_ID && proID != HeartbeatRqst.PRO_ID) {
                    return;
                }
            }
        }
        BaseRqst rqst= RqstPool.getNextInstance(proID);
        if(rqst == null)return;
        rqst.protocolID=proID;
        rqst.fromBytes(protocal.byteBuffer);
        ((BaseRqstCmd)rqst.cmd).execute(client, user, rqst);


        if(user == null){
            CpuDebuger.print(rqst+"rqstTime",1);
        }else{
            CpuDebuger.print(rqst+"rqstTime"+user.guid,1);
        }
        CpuDebuger.print(rqst+"rqstTime",1);
    }


}
