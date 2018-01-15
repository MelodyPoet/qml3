package talk;

import protocol.TalkRedPacketUserPVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jackie on 16-6-24.
 */
public class TalkRedPacketVo {

    public ArrayList<Integer> diamondList=new ArrayList<>();
    public ArrayList<TalkRedPacketUserPVo> users=new ArrayList<>();
    public  int totalDiamond;
    public  int totalCount;

    public HashMap<Long, Integer> allGet=new HashMap<>();
}
