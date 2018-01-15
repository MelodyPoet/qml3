package connect_to_clients;

import comm.Client;
import comm.Model;
import comm.QuickProtocal;
import comm.Utils;
import mlt_server_protocol.ServerTipRspd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

/*
* 1=type，tempID,devID,info 重要数据需要收到确认 否则一直重发
2=type,devID,info  不重要数据 不需要确认
3=type,revdTempID,devID 确实协议 不再需要确认和重发
* */
public class UdpServer extends Thread {
    public static final boolean WillStop = false;
    public static DatagramSocket ds;
    static DatagramPacket dpSendCheck;
    static DatagramPacket dpSend;
    //单条数据缓存 用户 非重要类型的发送 与广播
    public static  ByteBuffer tempbytes= ByteBuffer.wrap(new byte[1*1024]);
    public QuickProtocal currentProtocal = new QuickProtocal();
    static Set<Client> resendingSet = Collections.synchronizedSet(new HashSet());
    private int sOCKET_PORT;
    private Consumer<QuickProtocal> onRecvClient;
static int testMissCount=0;
    public static void init(int SOCKET_PORT, Consumer<QuickProtocal> onRecvClient) {
        UdpServer server = new UdpServer();
        server.sOCKET_PORT = SOCKET_PORT;
        server.onRecvClient = onRecvClient;
        dpSend = new DatagramPacket(new byte[0],0);
        dpSendCheck=new DatagramPacket(new byte[2],2);
        dpSendCheck.getData()[0]=3;
        server.start();
        new Thread(){
            @Override
            public void run() {
                super.run();
                AutoResendLoop();
            }


        }.start();
    }

    public static void sendToClient(QuickProtocal quickProtocal) throws IOException {
        long guid = quickProtocal.guid;

        Client client = Client.getOne(guid);
        if (client == null || client.currentUdp == null) {
            return;
        }
        if (quickProtocal.rqstType != 1) {

           // dpSend.setData(quickProtocal.byteBuffer.array(), quickProtocal.offset, quickProtocal.getDataLength() - quickProtocal.offset);
            sendRaw(quickProtocal.byteBuffer.array(), quickProtocal.offset,quickProtocal.getDataLength() - quickProtocal.offset,client.currentUdp);
          //  dpSend.setSocketAddress(client.currentUdp);

            try {
                ds.send(dpSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
byte[] bytes=Arrays.copyOfRange( quickProtocal.byteBuffer.array(),quickProtocal.offset,quickProtocal.getDataLength());

            sendImportantToClient(bytes,client,true);

        }


    }
public  static  void  sendRaw(byte[] bytes,int offset,int len,SocketAddress address){
        if(offset+len==0){
            dpSend.setData(bytes);
        }else{
            dpSend.setData(bytes,offset,len);
        }

    dpSend.setSocketAddress(address);
    try {
        ds.send(dpSend);
        System.out.println("sendToClient:"+address.toString());
    } catch (IOException e) {
        e.printStackTrace();

    }
}
    public static void castToClients(QuickProtocal quickProtocal) throws IOException {


        if(quickProtocal.castGroup==null){
            return;
        }

        byte[] bytes=Arrays.copyOfRange( quickProtocal.byteBuffer.array(),quickProtocal.offset,quickProtocal.getDataLength());
        System.out.println("castToClient:"+quickProtocal.castGroup.size()+":"+quickProtocal.proID);

        for (Long guid:quickProtocal.castGroup) {
            Client client=Client.getOne(guid);
            if (client == null || client.currentUdp == null) {
                continue;
            }
            if (quickProtocal.rqstType != 1) {
//if(client.offlineState!=Model.OfflineState_Online)continue;
               sendRaw(bytes,0,0,client.currentUdp);
            } else {

                //可以 优化 公用 tempid 就不需要复制
                byte[] bytesClone=Arrays.copyOf(bytes,bytes.length);

                sendImportantToClient(bytesClone,client,true);

            }



        }





    }
private  static void sendImportantToClient (byte [] bytes,Client client,boolean isNew){
        boolean sendNow=false;
         if(isNew){
             if(client.rspdImportantList.size()==0){
                 sendNow=true;
                 bytes[1]=client.getRspdTempID();
             }else {
                 bytes[1]=0;
             }
             client.rspdImportantList.add(bytes);
         }else{
             sendNow=true;
             bytes=client.rspdImportantList.peek();
             if(bytes[1]==0){
                 bytes[1]=client.getRspdTempID();
             }
         }

    if(sendNow) {


            resendingSet.add(client);

            client.resendCount++;
            if (client.resendCount > 100) {
                //离线处理
              //   return;
            }



        client.lastImportantSendTime = System.currentTimeMillis();

        dpSend.setData(bytes);
        dpSend.setSocketAddress(client.currentUdp);
          ByteBuffer bf=ByteBuffer.wrap(bytes);
          bf.position(2);
          int PID=bf.getShort();
        System.out.println("sendToClient2:" +PID+","+ client.currentUdp.toString() + ":" + bytes.length + ",listCount:" + client.rspdImportantList.size()+",waitime:"+client.waitTime);
        try {
            //     模拟rspd丢包

//            if (++testMissCount % 20 == 0) {
//                System.out.println("miss send2!");
//                return;
//            }
            ds.send(dpSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}


    private static void AutoResendLoop() {
    while (true){
        try {
            Thread.sleep(100);
            ArrayList<Client> tempList=new ArrayList();
            for (Client client:
            resendingSet) {
                if(client.rspdImportantList.size()==0){
                    tempList.add(client);
                }else if(client.lastImportantSendTime+client.waitTime*120/100+2< System.currentTimeMillis()){
                    client.waitTime=(client.waitTime*8+client.waitTime*2*2)/10;
                    if( client.waitTime>20000)client.waitTime=20000;
                    sendImportantToClient(null,client,false);
                }
            }
            for (Client client:tempList
                 ) {
                resendingSet.remove(client);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    }
    @Override
    public void run() {
        super.run();
        ByteBuffer bb = ByteBuffer.wrap(new byte[512]);
        ds = null;


        DatagramPacket dp = new DatagramPacket(bb.array(), bb.array().length);
        try {
            ds = new DatagramSocket(sOCKET_PORT);

        } catch (Exception e) {
            // LogManager.printError(e);
            e.printStackTrace();
        }

        while (true) {
            if (WillStop)
                break;
            try {
                bb.position(0);

                ds.receive(dp);

                //     模拟rqst丢包
                //if(Math.random()<0.4)continue;
//                if(++testid%3==0){
//                    System.out.println("miss recv!");
//                    continue;
//                }
                // if(JkTools.getRandBetween(0,100)<10)continue;
                currentProtocal.fromBytes(bb,dp.getLength());


                SocketAddress socketAddress = dp.getSocketAddress();
                System.out.println("revFromClient:"+socketAddress.toString()+":"+dp.getLength()+",tim:"+System.currentTimeMillis());


                Client client = Client.getOne(currentProtocal.guid);
                if (client == null) {
                    if(currentProtocal.proID!=2)//loginrqst.pid
                    {
                        new ServerTipRspd(socketAddress,(short) 994,null);
                        continue;
                    }
                    client = new Client();
                    client.guid = currentProtocal.guid;
                     Client.setOne(currentProtocal.guid, client);
                }

                if(client.currentUdp!=null&&!socketAddress.equals(client.currentUdp)){
                    if(currentProtocal.proID==2)//loginrqst.pid
                    {
                        new ServerTipRspd( client.currentUdp,(short) 996,null);

                    }else{
                      //  new ServerTipRspd(socketAddress,(short) 994,null);
                        continue;
                    }
                    client.lastRqstTempID=-1;
                    client.lastImportantSendTime=0;
                    client.rspdImportantList.clear();
                    client.waitTime=200;
                    client.offlineState= Model.OfflineState_Online;
                }
                client.currentUdp = socketAddress;
                client.lastRqstTime = System.currentTimeMillis();

                //rev checked
                if (currentProtocal.rqstType == 3) {

                    if (currentProtocal.rqstTempID == client.rspdTempID) {
                        // client.sending = false;
                        client.rspdImportantList.poll();
                        if( client.lastImportantSendTime!=0) {
                            int dt =(int)( System.currentTimeMillis() - client.lastImportantSendTime);
                            if(dt<client.waitTime*10) {
                                client.waitTime = (client.waitTime * 8 + dt * 2) / 10;
                                if(  client.waitTime>20000)client.waitTime=20000;
                            }
                        }
                        client.resendCount=0;
                        if(client.rspdImportantList.size()>0){
                            sendImportantToClient(null,client,false);
                        }


                    } else {
                        System.out.println("outdata check,checkTempid:"+currentProtocal.rqstTempID +",sendingTempID"+client.rspdTempID);
                    }
                    continue;
                }
                //收到重要的发确认消息
                if (currentProtocal.rqstType == 1) {
                    dpSendCheck.getData()[1] = currentProtocal.rqstTempID;
                    if (client.currentUdp == null) return;
                    dpSendCheck.setSocketAddress(client.currentUdp);
                    ds.send(dpSendCheck);
                    //已经收到的不再重复执行，可能重复发送的 只有 重要协议
                    if (client.lastRqstTempID == currentProtocal.rqstTempID) {

                        continue;
                    } else {
                        client.lastRqstTempID = currentProtocal.rqstTempID;
                    }
                } else {
                    // System.out.println("no important");
                }
                 if (currentProtocal.proID == 1) continue;//heartbeat

                onRecvClient.accept(currentProtocal);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


}
