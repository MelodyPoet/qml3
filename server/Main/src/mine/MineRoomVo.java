package mine;

import comm.CacheUserVo;
import protocol.PublicMinePVo;

import java.util.HashMap;

/**
 * Created by admin on 2017/7/13.
 */
public class MineRoomVo {
    public int commCount;
    public int highCount;
    public int rareCount;
    public int lastCommTime;
    public int lastMoreCommTime;
    public int lastHighTime;
    public int lastMoreHighTime;
    public int lastRareTime;
    public int lastMoreRareTime;
    public HashMap<Byte, PublicMinePVo> publicMap = new HashMap<>();
    public HashMap<Long,CacheUserVo> roomUsers = new HashMap<>();
}
