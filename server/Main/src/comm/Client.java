package comm;
import gluffy.comm.AbsClient;
import qmshared.QuickProtocal;
import qmshared.MQLogicServer;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;


public class Client extends AbsClient {
      public  CachePassportVo passportVo;
  public  boolean passportLogined=false;
    public  boolean roleLogined=false;


 public User currentUser;
    public HashMap<Byte, User> userMap=new HashMap<>();


    public static boolean createPassport(Client cl){
	//Client cl=new Client();
       cl.passportVo=  CachePassportVo.guidMap.get(cl.guid);
  if(cl.passportVo==null){
      cl.passportVo=new CachePassportVo();
      cl.passportVo.guid=cl.guid;

       CachePassportVo.guidMap.put(cl.passportVo.guid,cl.passportVo);
  }
   setOneAbs(cl.passportVo.guid,cl);
	return true;
	
}


public static Client getOne(long guid){
        return (Client)getOneAbs(guid);
}

    public static boolean isOnLine(CacheUserVo cacheUserVo){
    //是否在线统一等网关通知

//        Client other = Client.getOne(cacheUserVo.passportVo.devID,cacheUserVo.passportVo.serverID);
//        if(other == null || other.currentUser == null || other.currentUser.cacheUserVo.guid != cacheUserVo.guid)return false;
//        long time = System.currentTimeMillis() - other.lastRqstTime;
//        if(time>60000*5)return false;
        return true;
    }

    public static Client getOne(CacheUserVo cacheUserVo){
        if(cacheUserVo==null||cacheUserVo.passportVo==null)return null;
        Client other = (Client) Client.getOneAbs(cacheUserVo.passportVo.guid);
        if(other == null || other.currentUser == null || other.currentUser.cacheUserVo.guid != cacheUserVo.guid)return null;
        return other;
    }
}
