package mlt_server_protocol;

import comm.Client;
import comm.Utils;

import java.net.SocketAddress;


public class ServerTipRspd extends BaseRspd {
    public ServerTipRspd(SocketAddress socketAddress , short tipID, String param){
        super(socketAddress,(short)20,false);
     bytes.putShort(tipID);
Utils.writeString(bytes,param);

        finish();
    }


}