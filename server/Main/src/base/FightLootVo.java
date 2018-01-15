package base;

import protocol.PropPVo;

import java.util.ArrayList;
import java.util.HashMap;

public class FightLootVo {
    public  int mapID;
public int totalExp;
 public ArrayList<PropPVo> totalProps=new ArrayList<>();
    public HashMap<Short,ArrayList<PropPVo>> dynamicTotalProps= new HashMap<>();
    public int moneyAddRatio;
    public int expAddRatio;
    public int relifeCount;
    public int freeRelifeCount;
//    public byte equipID = -1;
//    public byte equipLevel = -1;
//    public byte equipQuality = -1;
//    public PropPVo equipPVo = null;
}
