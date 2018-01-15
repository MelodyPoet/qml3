package gluffy.comm;

import java.util.HashMap;


public abstract class AbsClient {
    public static HashMap<Long, AbsClient> allOnline=new HashMap<Long, AbsClient>();
    public static void setOneAbs(long guid,AbsClient client){
        allOnline.put(guid,client);
    }
    public static AbsClient getOneAbs(long guid){

        return allOnline.get(guid);

    }
     private String tempDev;//just for login
     private short serverID;
    public  long guid;
    public int addHours;
}
