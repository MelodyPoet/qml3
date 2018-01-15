package mlt_server_protocol;


import comm.Client;
import comm.MQGateServer;
import org.zeromq.ZMQ;


import java.nio.ByteBuffer;
import java.util.Arrays;

public class BaseRqst  {
	public static ByteBuffer bytes=ByteBuffer.wrap(new byte[1024*20]);


    public  short protocolID;



	public void fromBytes(ByteBuffer bytes) {

	}


	public void toBytes(ByteBuffer bytes) {

	}
public  void  readyForSend(Client client){
	bytes.clear();
	if(client!=null) {
		bytes.putLong(client.guid);
	}else {
		bytes.putLong(0);
	}
	bytes.put((byte)4);
	bytes.putShort(protocolID);
	toBytes(bytes);


	int len=bytes.position();
	bytes.limit(len);

}
public void  sendToLogic(ZMQ.Socket target){
	 MQGateServer.instance.sendToLogic(Arrays.copyOf(bytes.array(), bytes.position()),target);
	System.out.println("sendToGate3:"+protocolID);

}
	
}
