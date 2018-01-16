import comm.BaseRqstCmd;
import comm.Client;
import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;
import gluffy.utils.CpuDebuger;
import gluffy.utils.LogManager;
import protocol.LoginRqst;
import qmshared.MQLogicServer;
import qmshared.QuickProtocal;

public class MessageServer {
    static MQLogicServer mqServer;
  public   MessageServer() {

      try {
          RqstCmdModel.init();
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
      mqServer=new MQLogicServer(qmshared.Model.MainPort, value->
        {
            try{
                onRecvGate(value);
            }catch (Exception e){
                Client client= new Client();
                client.guid = value.guid;
               // new ServerTipRspd(client,(short)(992),"992");
                LogManager.printError(e);
            }
        });
        mqServer.start();
    }


    protected void onRecvGate( QuickProtocal protocal) {
        System.out.println("rev:pid="+protocal.proID);
        Client client= null;
        long uid=protocal.guid;

        CpuDebuger.init(1, 30);


        short proID= protocal.proID;

        // System.out.println("uid="+uid);

        //0 表示gate发的协议
        if(uid==0) {
        }else {
            client = Client.getOne(uid);
            if (client == null) {
                //客户端不存在且不是登陆请求 强制重登陆(一般为服务器重启后 或玩家离线太久后)
                 client=new Client();
                 client.guid=uid;
                 Client.setOneAbs(uid,client);
                if (proID != LoginRqst.PRO_ID) {

                } else {
                    client = new Client();
                    client.guid = uid;
                }
                if (client == null) return;

            }

        }
        BaseRqst rqst= RqstPool.getNextInstance(proID);
        if(rqst == null)return;
        rqst.protocolID=proID;
        rqst.fromBytes(protocal.byteBuffer);
        ((BaseRqstCmd)rqst.cmd).execute(client,null, rqst);
        CpuDebuger.print(rqst+"rqstTime"+uid,1);

    }
}
