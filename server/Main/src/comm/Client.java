package comm;
import gluffy.comm.AbsClient;
import qmshared.QuickProtocal;
import qmshared.MQLogicServer;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;


public class Client extends AbsClient {
   public  boolean passportLogined=false;
    public  boolean roleLogined=false;





public static Client getOne(long guid){
        return (Client)getOneAbs(guid);
}



}
