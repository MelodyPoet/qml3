package mlt_server_protocol;


import comm.QuickProtocal;
import connect_to_clients.UdpServer;


import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BaseRspd {

   public static ByteBuffer bytes=ByteBuffer.wrap(new byte[1024*20]);
     public  short protocolID;

  public   boolean isImportant=false;
     private SocketAddress socketAddress;


    public BaseRspd(){}
     public BaseRspd(SocketAddress socketAddress, short protocolID, boolean isImportant) {
        bytes.clear();
        this.isImportant=isImportant;
        this.socketAddress=socketAddress;
        this.protocolID = protocolID;


        //服务器直接转发时候用作 逻辑服务器id
        if(isImportant) {
            bytes.put((byte)(1));
            bytes.put((byte)0);//init for tempid
        }else {
            bytes.put((byte)(2));
        }
        bytes.putShort(protocolID);

    }

    protected void finish() {
        int len=bytes.position();
        bytes.limit(len);

            UdpServer.sendRaw(bytes.array(),0,len,socketAddress);

  }
}



