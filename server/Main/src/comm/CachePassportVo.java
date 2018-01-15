package comm;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jackie on 14-5-2.
 */
public class CachePassportVo {
     public static HashMap<Long, CachePassportVo> guidMap=new HashMap<>();
    public static HashSet<String> useTelphone=new HashSet<>();
    public long guid;
    //public  short serverID;
    public  byte clientTempID;//set by last login user
    //public String devID;
    public  byte vip;
    public byte lastRoleID=4;
    public String telphone;
    public String identifyCode;
    public int codeDeadTime;
    public byte isOldUser;
    public short loginTime;
    public int rmb;
    public String getVipAward = "";
    public int diamond;

    public HashMap<Byte, CacheUserVo> userMap=new HashMap<>();



}
