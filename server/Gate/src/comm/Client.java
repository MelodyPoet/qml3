package comm;

import org.eclipse.jetty.continuation.Continuation;

import javax.servlet.http.HttpServletResponse;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
     public static HashMap<Long, Client> allOnline = new HashMap<>();
     public long guid;
   // public String tempDev;//just for login
  //  public short serverID;

    //http info
    public HttpServletResponse currentHttp;
    public Continuation currentHttpCt;

    //udp info
    public SocketAddress currentUdp;
    public LinkedBlockingQueue<byte[]> rspdImportantList = new LinkedBlockingQueue<byte[]>();

    public byte rspdTempID = 0;
    public byte lastRqstTempID = -1;
    public long lastRqstTime = 0;

    public long lastImportantSendTime;
     public int waitTime =200;//动态调整的重发时间
public  int resendCount;
    public  int offlineState;
    public static Client getOne(long guid) {

            return allOnline.get(guid);

    }

    public static void setOne(long guid, Client client ) {

            allOnline.put(guid, client);

    }
    public byte getRspdTempID() {
        byte rst= ++rspdTempID;
        if(rst==0)return ++rspdTempID;
        return  rst;
    }

}
