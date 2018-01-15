import comm.ClientOfflineServer;
import comm.MQGateServer;
import comm.QuickProtocal;
import connect_to_clients.UdpServer;


public class GateServer {
    MQGateServer mqGateServer;


    public GateServer() {
        mqGateServer = new MQGateServer(protocal -> onRecvLogic(protocal));
        mqGateServer.start();
        new ClientOfflineServer().start();
        try {
            //udpServer=  new connect_to_clients.UdpServer();
            //udpServer.init(bytes -> handelClient(bytes));

            UdpServer.init(9091, quickProtocal -> onRecvClient(quickProtocal));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GateServer();


    }

    private void onRecvClient(QuickProtocal quickProtocal) {

        mqGateServer.sendToLogic(quickProtocal);
    }

    private void onRecvLogic(QuickProtocal quickProtocal) {
        try {
            System.out.println("onRecvLogic:proID:" + quickProtocal.proID + ",len:" + quickProtocal.getDataLength());
            if (quickProtocal.proID > 12800 && quickProtocal.proID <= 12899) {
               // ServerToServerHandle.handle(quickProtocal);
                return;
            }
            if (quickProtocal.castToClinet) {

                //  connect_to_clients.HttpServer.sendToClient(quickProtocal);
                UdpServer.castToClients(quickProtocal);

                return;
            }


            if (quickProtocal.rqstType == 4) {
//            //resend to server
                mqGateServer.sendToLogic(quickProtocal);

            } else

            {


                //  connect_to_clients.HttpServer.sendToClient(quickProtocal);
                UdpServer.sendToClient(quickProtocal);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}







