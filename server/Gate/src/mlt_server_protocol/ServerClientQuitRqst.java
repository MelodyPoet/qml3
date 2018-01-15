package mlt_server_protocol;

import java.nio.ByteBuffer;

public class ServerClientQuitRqst extends mlt_server_protocol.BaseRqst {
public static final short PRO_ID=12801;
public long clientID;

@Override
public void fromBytes(ByteBuffer bytes) {
 super.fromBytes(bytes);
clientID=bytes.getLong();

}

 @Override
 public void toBytes(ByteBuffer bytes) {
  super.toBytes(bytes);
bytes.putLong(clientID);

 }
}