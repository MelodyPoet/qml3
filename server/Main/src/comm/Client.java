package comm;
import gluffy.comm.AbsClient;
import modules.passport.RedisTablePassport;
import modules.scene.RedisTableFightMap;
import qmshared.QuickProtocal;
import qmshared.MQLogicServer;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;


public class Client extends AbsClient {
   public  boolean passportLogined=false;
    public  boolean roleLogined=false;


  public   RedisTablePassport rtPassport;
    public RedisTableFightMap rtFightMap;
public void initRedisTable(){
    rtPassport  =new RedisTablePassport(guid);
    rtFightMap =new RedisTableFightMap(guid);
}


public static Client getOne(long guid){
        return (Client)getOneAbs(guid);
}



}
