package gluffy.udp.core;


import gluffy.comm.AbsBaseRqstCmd;
import gluffy.comm.AbsClient;
import gluffy.comm.IBytes;
import qmshared.MQLogicServer;



import java.nio.ByteBuffer;
import java.util.Arrays;

public class BaseRqst implements IBytes {
	public static ByteBuffer bytes=ByteBuffer.wrap(new byte[1024*20]);

	  public AbsBaseRqstCmd cmd;
    public  short protocolID;


    @Override
	public void fromBytes(ByteBuffer bytes) {

	}

	@Override
	public void toBytes(ByteBuffer bytes) {

	}
public  void  sendToGate(AbsClient client){
	bytes.clear();
	if(client!=null) {
		bytes.putLong(client.guid);
	}else{
		bytes.putLong(0);
	}
	bytes.put((byte)4);
	bytes.putShort(protocolID);
	toBytes(bytes);


	int len=bytes.position();
	bytes.limit(len);
	MQLogicServer.sendToGate(Arrays.copyOf(bytes.array(), bytes.position()));
	System.out.println("sendToGate3:"+protocolID);
}
	
}
