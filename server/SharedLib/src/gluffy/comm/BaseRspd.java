package gluffy.comm;


import java.nio.ByteBuffer;

public class BaseRspd extends  gluffy.udp.core.BaseRspd implements IBytes {
	public BaseRspd(AbsClient client, short protocolID, boolean isImportant) {
	super(client,protocolID,isImportant);
	}


	@Override
	public void fromBytes(ByteBuffer bytes) {

	}

	@Override
	public void toBytes(ByteBuffer bytes) {

	}
}
