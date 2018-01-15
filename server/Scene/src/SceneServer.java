import comm.BaseRqstCmd;
import comm.Client;
import comm.SysLoop;
import gluffy.comm.AbsClient;
import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;
import gluffy.utils.CpuDebuger;
import protocol.MltSceneEnterRqst;
import qmshared.MQLogicServer;
import comm.Model;
import qmshared.QuickProtocal;

/**
 * Created by jackie on 14-4-16.
 */

public class SceneServer {
    //private static ArrayBlockingQueue<byte[]> readBytes=new ArrayBlockingQueue<>(50);
    private static byte[] reloginBytes;
    static MQLogicServer mqServer;

    public static void main(String[] args) throws Exception {
        Model.init();
        System.out.println(Model.MapBaseMap.size());
            // DynamicUserGroup.instance.init(Model.gameSetBaseVo.roomParas[0],Model.gameSetBaseVo.roomParas[1],Model.gameSetBaseVo.roomParas[2]);
        RqstCmdModel.init();


        new SceneServer();

    }

    SceneServer() {


    mqServer=new  MQLogicServer(Model.ScenePort, value->
    {
        try {
            onRecvGate(value);
        }catch (Exception e){
            e.printStackTrace();
        }

    });
    mqServer.start();
    new SysLoop().start();
}


    protected void onRecvGate( QuickProtocal protocal) {
        System.out.println("rev:pid="+protocal.proID);

        CpuDebuger.init(1, 30);



        long guid=protocal.guid;
         short proID= protocal.proID;
        AbsClient client=null;
        // System.out.println("uid="+uid);
        //0 表示gate发的协议
        if(guid==0){

        }else {
            client = AbsClient.getOneAbs(guid);
            if (client == null) {
                if (proID != MltSceneEnterRqst.PRO_ID) return;
                client = new Client();
                client.guid = guid;
                AbsClient.setOneAbs(guid, client);

            }
        }



        BaseRqst rqst= RqstPool.getNextInstance(proID);
        if(rqst == null)return;
        rqst.protocolID=proID;
        rqst.fromBytes(protocal.byteBuffer);
        ((BaseRqstCmd) rqst.cmd).execute((Client) client, rqst);

    }


}
