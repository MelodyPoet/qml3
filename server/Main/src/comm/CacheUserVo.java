package comm;

import arena.RankUserArenaVo;
import base.AttributeModel;
import base.SkillModel;
import dragon.DragonCacheModel;
import dragon.DragonEggModel;
import friend.FriendCacheModel;
import gang.Gang;
import gang.RankGangUserVo;

import mine.MineModel;
import mine.RankMineVo;
import protocol.GangGuessLogPVo;
import protocol.HeroTagPVo;
import protocol.PropPVo;
import protocol.StonePVo;
import rank.RankUserFbVo;
import rank.RankUserZdlVo;
import redness.RankRednessVo;
import skin.SkinModel;
import table.RankEnum;
import wing.WingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jackie on 14-5-2.
 */
public class CacheUserVo {

    public static HashMap<Long, CacheUserVo> allMap=new HashMap<>();
    public static HashSet<String> usedName=new HashSet<>();


    public  boolean isRobot(){return guid==0;};
    public long guid;
    public CachePassportVo passportVo;
    public User onlineUser;
    public  byte level;
    public short skin;
    public byte baseID;
    public  int zdl;
    public  int lastMap;
    public int lastLoginTime;
    public int wormNestMaxFloor;
    public int wormNestMaxFloorTime;
    public  int arenaScore;
    public int fbScore;
    public int snatch_safe_time;
    public byte gangStatus;
    public String gangName;
    public RankUserZdlVo rankVo;
    public RankUserFbVo rankFbVo;
    public RankUserArenaVo rankArenaVo;
    public RankGangUserVo rankGangUserVo;
    public RankRednessVo rankRednessVo;
    public RankMineVo rankMineVo;
    public String name;
    public Gang gang = new Gang();

    public PropPVo[] equipItems = new PropPVo[9];

    public HashMap<Byte, StonePVo> equipDragonStone;
    public SkillModel skillModel;
    public DragonEggModel dragonEggModel;
    public int yunwaID;
    public int newSysInfoCount;
    public SkinModel skinModel;
    public WingModel wingModel;

    public DragonCacheModel dragonCacheModel;
    public ArrayList<GangGuessLogPVo> addGuessLogList;
    public byte guessDouble;
    public FriendCacheModel friendCacheModel;
    public AttributeModel attributeModel;
    public byte portrait;
    public byte status;
    public MineModel mineModel;
    public short heroTagID;
    public ArrayList<HeroTagPVo> addHeroTagList;
    public CacheUserVo() {
        equipDragonStone=new HashMap<>();
        skillModel=new SkillModel();
        wingModel=new WingModel();
        dragonEggModel = new DragonEggModel();
        skinModel = new SkinModel();
        dragonCacheModel = new DragonCacheModel();
        addGuessLogList = new ArrayList<>();
        guessDouble = 1;
        friendCacheModel = new FriendCacheModel();
        attributeModel = new AttributeModel();
        mineModel = new MineModel();
        addHeroTagList = new ArrayList<>();
    }

    public RankListItem getRankVo(byte rankType) {
        switch (rankType){
            case RankEnum.ZDL:
                return rankVo;
            case  RankEnum.DUNGEON:
                return rankFbVo;
            case  RankEnum.ARENA:
                return rankArenaVo;
            case  RankEnum.GangUSER:
                return rankGangUserVo;
            case RankEnum.REDNESS:
                return rankRednessVo;
            case RankEnum.MINE:
                return rankMineVo;
        }
        return null;
    }

    public int getRankIndex(byte rankType) {
                 int myIndex = -1;
                 RankListItem item =  getRankVo( rankType);
               if (item != null) myIndex = item.orderIndex;
        return myIndex;

    }

    public int getRankScore(byte rankType){
        int myScore = 0;
        RankListItem item =  getRankVo( rankType);
        if (item != null) myScore = item.orderScore();
        return myScore;
    }
}
