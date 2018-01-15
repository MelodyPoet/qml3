
package comm;

import mlt_server_protocol.ServerClientQuitRqst;

import java.util.Iterator;

public class ClientOfflineServer extends Thread {
final  static  int TIME_LEAVE=30*1000,TIME_QUIT=60*1000*5;



    @Override
    public void run() {
        super.run();


        while (true) {
            try {
                Thread.sleep(3000);
                Iterator<Client> it = Client.allOnline.values().iterator();
                long ct=System.currentTimeMillis();

                while(it.hasNext()){
                    Client cl = it.next();
                 long dt=   ct- cl.lastRqstTime;
                    if(dt>TIME_QUIT){

                        if(cl.offlineState!=Model.OfflineState_Quit) {
                            cl.offlineState=Model.OfflineState_Quit;
                            ServerClientQuitRqst scqRqst=new ServerClientQuitRqst();
                            scqRqst.protocolID=ServerClientQuitRqst.PRO_ID;
                            scqRqst.clientID=cl.guid;
                            scqRqst.readyForSend(null);
                            scqRqst.sendToLogic(MQGateServer.instance.toMain);
                            scqRqst.sendToLogic(MQGateServer.instance.toScene);

                        }

                        it.remove();
                    }else if(dt>TIME_LEAVE){
                        if(cl.offlineState!=Model.OfflineState_Leave) {
                            cl.offlineState=Model.OfflineState_Leave;
                            //if(cl.waitTime<5000)cl.waitTime=5000;
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
