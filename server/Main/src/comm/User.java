package comm;

import airing.AiringModel;
import arena.ArenaModel;
import base.*;
import dragon.DragonCacheModel;
import dragon.DragonModel;
import elit.EliteModel;
import emperor.EmperorModel;
import endless.wormNest.WormNestModel;
import friend.FriendModel;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gang.RankGangUserVo;
import gang.gangBuild.GangSkillModel;
import giftCode.GiftCodeModel;
import gluffy.comm.AbsUser;
import gluffy.utils.JkTools;
import heroTag.HeroTagModel;
import heroTag.HeroTagVo;
import illustrated.IllustratedModel;
import mail.LoopSendMailModel;
import mail.MailModel;
import mine.MineModel;
import mine.MineRecruitVo;
import mine.RankMineVo;
import mission.LimitTimeModel;
import mission.MissionModel;
import prop.PropLogVo;
import prop.PropModel;
import protocol.*;
import rank.RankModel;
import realm.RealmModel;
import realm.RealmVo;
import recharge.RechargeModel;
import redness.RankRednessVo;
import redness.RednessModel;
import sendMsg.ClosedTestModel;
import sign.SignModel;
import snatch.SnatchModel;
import sqlCmd.AllSql;
import suit.PropSuitModel;
import table.*;
import talk.GetTalkBackCmd;
import talk.UserNewMsgItem;


import java.util.*;

public class User extends AbsUser{
    public static final int LIMIT_CD_COUNT = 3;
    public PropModel propModel;
    public byte baseID;
    public RoleBaseVo baseVo;
    public int[] userdata;
    public int lastWeekDay;

    public int zdl;

    public CacheUserVo cacheUserVo;
    public HashMap<Integer, MapQualityPVo> mapQualityMap;
    public HashMap<Short, MapEnteredPVo> mapEnteredMap;
    public FightLootVo fightingLoot;
    public MapBaseVo currentMap;
    public int chaperScore = 0;
    public Client client;
    public UserActState actState;
    public long guid;
    public boolean sqlInited;
    public UserNewMsgItem userNewMsgItem;
    public int roomID = -1;
    public byte hadGetTime = -1;
    public byte isGetCloseTest;
    public String guide = "";
    public int dragonStoneZDL;
    public String dragonStoneOwnLevel = "";
    public String buyVipGift = "";
    public String getTili = "";
    public String channelStr= "";
    public ArenaModel arenaModel;
    public MissionModel missionModel;//主线任务
    public MissionModel activationModel;//每日任务 活动
    public MissionModel achieveModel;//成就型任务
    public HashMap<Byte,MissionModel> limitTimeMap;//限时活动
    public HashSet<Byte> timeOutSet;//已完成或已过期的限时活动
    public SignModel signModel;
    public DragonModel dragonModel;
    public SnatchModel snatchModel;
    public WormNestModel wormNestModel;
    public MailModel mailModel;
    public IllustratedModel illustratedModel;
    public GangSkillModel gangSkillModel;
    public AiringModel airingModel;
    public HashMap<Byte, ByteIntPVo> equipQualityMap;
    public HashMap<Integer, IntIntPVo> carnetCountMap;
    public HashMap<Byte, ByteIntPVo> stoneCountMap;
    public HashMap<Byte, ByteIntPVo> eggQualityMap;
    public HashSet<Byte> functionSet;
    public RednessModel rednessModel;
    public HashSet<Integer> notEnterRoom;
    public FriendModel friendModel;
    public  int [] heroAttributes;
    public UserUpdateModel userUpdateModel;
    public EliteModel eliteModel;
    public GiftCodeModel giftCodeModel;
    public RechargeModel rechargeModel;
    public int nextUpdataTime;
    public HeroTagModel heroTagModel;
    public EmperorModel emperorModel;
    public RealmModel realmModel;

    public void initGame() {

        baseVo = Model.RoleBaseMap.get((int) baseID);

    }

    public User(Client client) {
        this.client = client;

        userdata = new int[UserDataEnum.MAX];
        mapQualityMap = new HashMap<>();
        propModel = new PropModel(this);
        actState = UserActState.CAMP;
        arenaModel = new ArenaModel(this);
        missionModel = new MissionModel(this, MissionTypeEnum.MAIN);
        activationModel = new MissionModel(this, MissionTypeEnum.DAILY);
        achieveModel = new MissionModel(this, MissionTypeEnum.ACHIEVE);
        signModel = new SignModel(this);
        dragonModel = new DragonModel(this);
        snatchModel = new SnatchModel(this);
        wormNestModel = new WormNestModel(this);
        mapEnteredMap = new HashMap<>();
        mailModel = new MailModel(this);
        userNewMsgItem = new UserNewMsgItem(this);
        illustratedModel = new IllustratedModel(this);
        gangSkillModel = new GangSkillModel(this);
        airingModel = new AiringModel(this);
        equipQualityMap = new HashMap<>();
        carnetCountMap = new HashMap<>();
        stoneCountMap = new HashMap<>();
        eggQualityMap = new HashMap<>();
        functionSet = new HashSet<>();
        limitTimeMap = new HashMap<>();
        timeOutSet = new HashSet<>();
        rednessModel=new RednessModel(this);
        notEnterRoom = new HashSet<>();
        friendModel = new FriendModel(this);
      //  worldBossModel=new WorldBossModel(this);
        userUpdateModel = new UserUpdateModel(this);
        eliteModel = new EliteModel(this);
        giftCodeModel = new GiftCodeModel(this);
        rechargeModel = new RechargeModel(this);
        heroTagModel = new HeroTagModel(this);
        emperorModel = new EmperorModel(this);
        realmModel = new RealmModel(this);
        heroAttributes = new int[AttributeEnum.MAX];
    }

    public void initWithCache() {
        cacheUserVo.onlineUser=this;
        cacheUserVo.skillModel.initWithCache(this);
        cacheUserVo.dragonEggModel.initWithCache(this);
        cacheUserVo.skinModel.initWithCache(this);
        cacheUserVo.dragonCacheModel.initWithCache(this);
        cacheUserVo.attributeModel.initWithCache(this);
        cacheUserVo.mineModel.initWithCache(this);
        cacheUserVo.wingModel.initWithCache(this);
    }

    public int getUserData(short type) {
        return userdata[type];
    }
//初始化不能用这些函数 否则反复写库影响效率

    public int setUserData(short type, int value) {
        return setUserData(type, value,false);
    }

    public int setUserData(short type, int value,boolean sendRspd) {
        return setUserData(type, value,sendRspd,0);
    }

    public int setUserData(short type, int value, boolean sendRspd,int reasonType) {
        if (type == UserDataEnum.LEVEL) {
            UserUpdateBaseVo extraBaseVo = Model.UserUpdateBaseMap.get(1);

            if (extraBaseVo != null && value > extraBaseVo.max) value = extraBaseVo.max;
        }
        boolean propLog = false;
        int oldValue = userdata[type];
        int intNewValue = userdata[type] = value;
        if (sendRspd) {
            new UpdateUserDataRspd(client, type, intNewValue);
        }
        switch (type) {
            case UserDataEnum.MONEY:
                propLog = true;
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_MONEY, intNewValue);
                }
                break;
            case UserDataEnum.DIAMOND:
                propLog = true;
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_DIAMOND, intNewValue);
                }
                break;
            case UserDataEnum.EXP:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_EXP, intNewValue);
                }
                break;
            case UserDataEnum.LEVEL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LEVEL, intNewValue);
                }
                levelAiring();
                cacheUserVo.level = (byte) intNewValue;
                int count = limitTimeMap.size();
                for(LimitTimeActivationBaseVo vo : LimitTimeModel.levelList){
                    if(cacheUserVo.level < vo.triggeParam || limitTimeMap.containsKey((byte)vo.ID) || timeOutSet.contains((byte)vo.ID))continue;
                    MissionModel missionModel = new MissionModel(this,(byte)vo.ID);
                    limitTimeMap.put((byte)vo.ID,missionModel);
                    missionModel.startTime = JkTools.getGameServerTime(client);
                    missionModel.saveSqlData();
                }
                if(limitTimeMap.size() > count){
                    MissionModel.sendLimitTimeInfo(this);
                }
                activationModel.calculateNewMission();
//                activationModel.progressBuyAct(MissionConditionEnum.GIFT_7DAY, getCreatedDays(), intNewValue);
                checkFunctionOpen(false, true);
                if(getUserData(UserDataEnum.LEVEL)>1){
                    for(UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()){
                        if(vo.dataType == 1 && vo.dataID == UserDataEnum.TILIZHI){
                            int max = getUserVoAddMax(vo);
                            int tilizhi = Math.min(getUserData(UserDataEnum.TILIZHI)+Model.GameSetBaseMap.get(17).intValue,max);
                            setUserData(UserDataEnum.TILIZHI,tilizhi,true);
                            int time = JkTools.getGameServerTime(null)+vo.getBySeconds;
                            if(tilizhi == max){
                                time = 0;
                            }
                            setUserData(UserDataEnum.NEXTTIME_Tili,time,true);
                            break;
                        }
                    }
                }
                break;
//            case UserDataEnum.SKIN:
//                 cacheUserVo.skin=(byte)intNewValue;
//                 break;
            case UserDataEnum.TILIZHI:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_TILIZHI, intNewValue);
                }
                break;
            case UserDataEnum.LAST_DAY:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_RESETDAY, intNewValue);
                }
                break;

            case UserDataEnum.HP_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_HPDRUG, intNewValue);
                }
                break;
            case UserDataEnum.NEXTTIME_SNATCH_SAFE:
                cacheUserVo.snatch_safe_time = value;
                break;
            case UserDataEnum.BAG_CELL_HAVE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_BAG_CELL_HAVE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.BAG_CELL_CD_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_BAG_CELL_CD_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.NEXTTIME_BAG_CELL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_NEXTTIME_BAG_CELL, intNewValue);
                }
                break;
            case UserDataEnum.BAG_REMOVE_CD_TIME:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_BAG_REMOVE_CD_TIME, intNewValue);
                }
                break;
            case UserDataEnum.LAST_LOGIN_TIME:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LAST_LOGIN_TIME, intNewValue);
                    this.cacheUserVo.lastLoginTime = intNewValue;
                }
                break;
            case UserDataEnum.WormNestCurrentFloor:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_WORM_NEST_CURRENT_FLOOR, intNewValue);
                }
                break;
            case UserDataEnum.WormNestMaxFloor:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_WORM_NEST_MAX_FLOOR, intNewValue);
                }
                break;
            case UserDataEnum.WormNestMaxFloorTime:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_WORM_NEST_MAX_FLOOR_TIME, intNewValue);
                }
                break;
            case UserDataEnum.WormNestNextEndTime:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_WORM_NEST_NEXT_END_TIME, intNewValue);
                }
                break;
            case UserDataEnum.WormNestResetTime:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_WORM_NEST_RESET_TIME, intNewValue);
                }
                break;
            case UserDataEnum.IsGetGangAward:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_IS_GET_GANG_AWARD, intNewValue);
                }
                break;
            case UserDataEnum.Talent_CD:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_TALENT_CD, intNewValue);
                }
                break;
            case UserDataEnum.ARENA_FIGHT_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_ARENA_FIGHT_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.EXCHANGE_TILI_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_EXCHANGE_TILI_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.CREDIT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_CREDIT, intNewValue);
                }
                break;
            case UserDataEnum.DRAGON_STONE_BUY_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_DRAGON_STONE_BUY_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.PropShopFreeFlushTime:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_PROP_SHOP_FREE_FLUSH_TIME, intNewValue);
                }
                break;
            case UserDataEnum.PropShopFlushCD:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_PROP_SHOP_FLUSH_CD, intNewValue);
                }
                break;
            case UserDataEnum.LJ_REFINE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_REFINE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_SNATCH_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_SNATCH_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_ARENA_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_ARENA_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_STARS_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_STARS_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_COST_MONEY_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_COST_MONEY_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_COST_DIAMOND_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_COST_DIAMOND_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_RECHARGE_DIAMOND_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_RECHARGE_DIAMOND_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_SIGN_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_SIGN_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DRAGON_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DRAGON_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DISSOLVE_EQUIP_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DISSOLVE_EQUIP_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_REDPACKET_VALUE:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_REDPACKET_VALUE, intNewValue);
                }
                break;
            case UserDataEnum.LJ_MONEY_BUY_DRAGON_STONE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_MONEY_BUY_DRAGON_STONE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_MONEY_BUY_DRAGON_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_MONEY_BUY_DRAGON_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_DRAGON_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DIAMOND_BUY_DRAGON_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_BUY_TILI_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_BUY_TILI_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_UP_DRAGON_STONE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_UP_DRAGON_STONE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_TOUCH_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_TOUCH_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_EQUIP_INTENSIFY_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_EQUIP_INTENSIFY_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_SHOP_FLUSH_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_SHOP_FLUSH_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_TALK_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_TALK_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_LIKE_TALK_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_LIKE_TALK_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_NORMAL_PASS_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_NORMAL_PASS_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_YOU_MIN_PASS_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_YOU_MIN_PASS_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_YUAN_SU_PASS_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_YUAN_SU_PASS_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DRAGON_PASS_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DRAGON_PASS_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.MAX_DRAGON_LEVEL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.MAX_DRAGON_LEVEL, intNewValue);
                }
                break;
            case UserDataEnum.MAX_EQUIP_INTENSIFY_LEVEL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.MAX_EQUIP_INTENSIFY_LEVEL, intNewValue);
                }
                break;
            case UserDataEnum.MAX_JEWELRY_INTENSIFY_LEVEL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.MAX_JEWELRY_INTENSIFY_LEVEL, intNewValue);
                }
                break;
            case UserDataEnum.MAX_TALENT_LEVEL:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.MAX_TALENT_LEVEL, intNewValue);
                }
                break;
            case UserDataEnum.LAST_RUN_DAY:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LAST_RUN_DAY, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_ONE_DRAGON_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.LJ_DIAMOND_BUY_ONE_DRAGON_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.NEXTTIME_Tili:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_NEXT_TIME_TILI, intNewValue);
                }
                break;
            case UserDataEnum.EXCHANGE_ARENA_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_EXCHANGE_ARENA_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.NEXT_TIME_JOIN_GANG:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_NEXT_TIME_JOIN_GANG, intNewValue);
                }
                break;
            case UserDataEnum.CAN_TOUCH_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_CAN_TOUCH_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.YEAR_CARD:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_YEAR_CARD, intNewValue);
                }
                break;
            case UserDataEnum.MONTH_CARD:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_MONTH_CARD, intNewValue);
                }
                break;
            case UserDataEnum.YEAR_CARD_END_TIME:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_YEAR_CARD_END_TIME, intNewValue);
                }
                break;
            case UserDataEnum.MONTH_CARD_END_TIME:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_MONTH_CARD_END_TIME, intNewValue);
                }
                break;
            case UserDataEnum.MONEY_TREE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_MONEY_TREE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.MONEY_TREE_NEXT_TIME:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_MONEY_TREE_NEXT_TIME, intNewValue);
                }
                break;
            case UserDataEnum.NORMAL_RESET_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_NORMAL_RESET_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.DAILY_LIVENESS:
                addUserData(UserDataEnum.LJ_LIVENESS, intNewValue, true);
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_DAILY_LIVENESS, intNewValue);
                }
                break;
            case UserDataEnum.LIVENESS:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LIVENESS, intNewValue);
                }
                break;
            case UserDataEnum.LJ_TALENT_UP_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_TALENT_UP_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DRAGON_EGG_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_DRAGON_EGG_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_HATCH_DRAGON_STONE:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_HATCH_DRAGON_STONE, intNewValue);
                }
                break;
            case UserDataEnum.LJ_LIVENESS:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_LIVENESS, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DRAGON_STONE_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_DRAGON_STONE_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.LJ_DRAGON_UP_COUNT:
                if (sendRspd) {
                    AllSql.userSql.update(this, AllSql.userSql.FIELD_LJ_DRAGON_UP_COUNT, intNewValue);
                }
                break;
            case UserDataEnum.RED_NESS_MONEY:
                if (sendRspd) {

                    AllSql.userSql.update(this, AllSql.userSql.FIELD_RED_NESS_MONEY, intNewValue);
                }
                break;
            case UserDataEnum.ELITE_BUY_COUNT:
                if(sendRspd){
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_ELITE_BUY_COUNT,intNewValue);
                }
                break;
            case UserDataEnum.MAX_WORLD_BOSS_HURT:
                if(sendRspd){
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_MAX_WORLD_BOSS_HURT,intNewValue);
                }
                break;
            case UserDataEnum.LJ_WORLD_BOSS_HURT:
                if(sendRspd){
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_LJ_WORLD_BOSS_HURT,intNewValue);
                }
                break;
            case UserDataEnum.OPEN_EQUIP_BAG_COUNT:
                if(sendRspd){
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_OPEN_EQUIP_BAG_COUNT,intNewValue);
                }
                break;
            case UserDataEnum.EQUIP_BAG_COUNT:
                if(sendRspd){
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_EQUIP_BAG_COUNT,intNewValue);
                }
                break;
            case UserDataEnum.MR_MINE_COUNT:
                if(intNewValue > 0){
                    if(cacheUserVo.rankMineVo == null){
                        RankMineVo rankMineVo = new RankMineVo(cacheUserVo);
                        rankMineVo.orderIndex = -1;
                    }
                    RankModel.rankMineList.SortWithLimit(cacheUserVo.rankMineVo,MineModel.RANK_COUNT,RankEnum.MINE,intNewValue);
                    cacheUserVo.rankMineVo.mineCount = intNewValue;
                }
                AllSql.userSql.update(this,AllSql.userSql.FIELD_MR_MINE_COUNT,intNewValue);
                break;
            case UserDataEnum.MR_EXPLOIT_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_MR_EXPLOIT_COUNT,intNewValue);
                break;
            case UserDataEnum.MR_SEARCH_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_MR_SEARCH_COUNT,intNewValue);
                break;
            case UserDataEnum.MR_FREE_RECRUIT_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_MR_FREE_RECRUIT_COUNT,intNewValue);
                break;
            case UserDataEnum.MR_LOOT_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_MR_LOOT_COUNT,intNewValue);
                break;
            case UserDataEnum.LotteryPropFreeCount:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LOTTERY_PROP_FREE_COUNT,intNewValue);
                break;
            case UserDataEnum.LotteryStoneCount:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LOTTERY_STONE_COUNT,intNewValue);
                break;
            case UserDataEnum.LotteryDragonCount:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LOTTERY_DRAGON_COUNT,intNewValue);
                break;
            case UserDataEnum.LotteryStoneNextTime:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LOTTERY_STONE_NEXTTIME,intNewValue);
                break;
            case UserDataEnum.LotteryDragonNextTime:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LOTTERY_DRAGON_NEXTTIME,intNewValue);
                break;
            case UserDataEnum.SNATCH_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_SNATCH_FIGHT_COUNT,intNewValue);
                break;
            case UserDataEnum.SNATCH_BUY_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_NEXTTIME_SNATCH,intNewValue);
                break;
            case UserDataEnum.TALENT_EXP:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_TALENT_EXP,intNewValue);
                break;
            case UserDataEnum.LJ_LOTTERY_COUNT:
                AllSql.userSql.update(this,AllSql.userSql.FIELD_LJ_LOTTERY_COUNT,intNewValue);
                break;
        }
        if(propLog){
            PropLogVo propLogVo = new PropLogVo();
            propLogVo.userID = guid;
            propLogVo.propID = type;
            propLogVo.count = intNewValue - oldValue;
            propLogVo.rensonType = reasonType;
            propLogVo.createTime = JkTools.getGameServerTime(null);
            AllSql.propLogSql.insertNew(propLogVo);
        }
        return intNewValue;
    }

    public static void setHisData(long userID,byte type, int value) {
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
        if (type == UserDataEnum.LEVEL) {
            UserUpdateBaseVo extraBaseVo = Model.UserUpdateBaseMap.get(1);

            if (extraBaseVo != null && value > extraBaseVo.max) value = extraBaseVo.max;
        }
        switch (type) {
            case UserDataEnum.MONEY:
                AllSql.userSql.update(userID, AllSql.userSql.FIELD_MONEY, value);
                break;
            case UserDataEnum.DIAMOND:
                AllSql.userSql.update(userID, AllSql.userSql.FIELD_DIAMOND, value);
                break;
            case UserDataEnum.EXP:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_EXP, value);
                break;
            case UserDataEnum.LEVEL:
                AllSql.userSql.update(userID, AllSql.userSql.FIELD_LEVEL, value);
                break;
//            case UserDataEnum.SKIN:
//                 cacheUserVo.skin=(byte)intNewValue;
//                 break;
            case UserDataEnum.TILIZHI:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_TILIZHI, value);
                break;
            case UserDataEnum.LAST_DAY:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_RESETDAY, value);
                break;

            case UserDataEnum.HP_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_HPDRUG, value);
                break;
            case UserDataEnum.NEXTTIME_SNATCH_SAFE:
                if(cacheUserVo != null){
                    cacheUserVo.snatch_safe_time = value;
                }
                break;
            case UserDataEnum.BAG_CELL_HAVE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_BAG_CELL_HAVE_COUNT, value);
                break;
            case UserDataEnum.BAG_CELL_CD_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_BAG_CELL_CD_COUNT, value);
                break;
            case UserDataEnum.NEXTTIME_BAG_CELL:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_NEXTTIME_BAG_CELL, value);
                break;
            case UserDataEnum.BAG_REMOVE_CD_TIME:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_BAG_REMOVE_CD_TIME, value);
                break;
            case UserDataEnum.LAST_LOGIN_TIME:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LAST_LOGIN_TIME, value);
                if(cacheUserVo != null){
                    cacheUserVo.lastLoginTime = value;
                }
                break;
            case UserDataEnum.WormNestCurrentFloor:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_WORM_NEST_CURRENT_FLOOR, value);
                break;
            case UserDataEnum.WormNestMaxFloor:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_WORM_NEST_MAX_FLOOR, value);
                break;
            case UserDataEnum.WormNestMaxFloorTime:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_WORM_NEST_MAX_FLOOR_TIME, value);
                break;
            case UserDataEnum.WormNestNextEndTime:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_WORM_NEST_NEXT_END_TIME, value);
                break;
            case UserDataEnum.WormNestResetTime:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_WORM_NEST_RESET_TIME, value);
                break;
            case UserDataEnum.IsGetGangAward:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_IS_GET_GANG_AWARD, value);
                break;
            case UserDataEnum.Talent_CD:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_TALENT_CD, value);
                break;
            case UserDataEnum.ARENA_FIGHT_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_ARENA_FIGHT_COUNT, value);
                break;
            case UserDataEnum.EXCHANGE_TILI_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_EXCHANGE_TILI_COUNT, value);
                break;
            case UserDataEnum.CREDIT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_CREDIT, value);
                break;
            case UserDataEnum.DRAGON_STONE_BUY_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_DRAGON_STONE_BUY_COUNT, value);
                break;
            case UserDataEnum.PropShopFreeFlushTime:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_PROP_SHOP_FREE_FLUSH_TIME, value);
                break;
            case UserDataEnum.PropShopFlushCD:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_PROP_SHOP_FLUSH_CD, value);
                break;
            case UserDataEnum.LJ_REFINE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_REFINE_COUNT, value);
                break;
            case UserDataEnum.LJ_SNATCH_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_SNATCH_COUNT, value);
                break;
            case UserDataEnum.LJ_ARENA_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_ARENA_COUNT, value);
                break;
            case UserDataEnum.LJ_STARS_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_STARS_COUNT, value);
                break;
            case UserDataEnum.LJ_COST_MONEY_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_COST_MONEY_COUNT, value);
                break;
            case UserDataEnum.LJ_COST_DIAMOND_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_COST_DIAMOND_COUNT, value);
                break;
            case UserDataEnum.LJ_RECHARGE_DIAMOND_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_RECHARGE_DIAMOND_COUNT, value);
                break;
            case UserDataEnum.LJ_SIGN_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_SIGN_COUNT, value);
                break;
            case UserDataEnum.LJ_DRAGON_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DRAGON_COUNT, value);
                break;
            case UserDataEnum.LJ_DISSOLVE_EQUIP_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DISSOLVE_EQUIP_COUNT, value);
                break;
            case UserDataEnum.LJ_REDPACKET_VALUE:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_REDPACKET_VALUE, value);
                break;
            case UserDataEnum.LJ_MONEY_BUY_DRAGON_STONE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_MONEY_BUY_DRAGON_STONE_COUNT, value);
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT, value);
                break;
            case UserDataEnum.LJ_MONEY_BUY_DRAGON_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_MONEY_BUY_DRAGON_COUNT, value);
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_DRAGON_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DIAMOND_BUY_DRAGON_COUNT, value);
                break;
            case UserDataEnum.LJ_BUY_TILI_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_BUY_TILI_COUNT, value);
                break;
            case UserDataEnum.LJ_UP_DRAGON_STONE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_UP_DRAGON_STONE_COUNT, value);
                break;
            case UserDataEnum.LJ_TOUCH_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_TOUCH_COUNT, value);
                break;
            case UserDataEnum.LJ_EQUIP_INTENSIFY_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_EQUIP_INTENSIFY_COUNT, value);
                break;
            case UserDataEnum.LJ_SHOP_FLUSH_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_SHOP_FLUSH_COUNT, value);
                break;
            case UserDataEnum.LJ_TALK_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_TALK_COUNT, value);
                break;
            case UserDataEnum.LJ_LIKE_TALK_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_LIKE_TALK_COUNT, value);
                break;
            case UserDataEnum.LJ_NORMAL_PASS_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_NORMAL_PASS_COUNT, value);
                break;
            case UserDataEnum.LJ_YOU_MIN_PASS_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_YOU_MIN_PASS_COUNT, value);
                break;
            case UserDataEnum.LJ_YUAN_SU_PASS_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_YUAN_SU_PASS_COUNT, value);
                break;
            case UserDataEnum.LJ_DRAGON_PASS_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DRAGON_PASS_COUNT, value);
                break;
            case UserDataEnum.MAX_DRAGON_LEVEL:
                    AllSql.userSql.update(userID, AllSql.userSql.MAX_DRAGON_LEVEL, value);
                break;
            case UserDataEnum.MAX_EQUIP_INTENSIFY_LEVEL:
                    AllSql.userSql.update(userID, AllSql.userSql.MAX_EQUIP_INTENSIFY_LEVEL, value);
                break;
            case UserDataEnum.MAX_JEWELRY_INTENSIFY_LEVEL:
                    AllSql.userSql.update(userID, AllSql.userSql.MAX_JEWELRY_INTENSIFY_LEVEL, value);
                break;
            case UserDataEnum.MAX_TALENT_LEVEL:
                    AllSql.userSql.update(userID, AllSql.userSql.MAX_TALENT_LEVEL, value);
                break;
            case UserDataEnum.LAST_RUN_DAY:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LAST_RUN_DAY, value);
                break;
            case UserDataEnum.LJ_DIAMOND_BUY_ONE_DRAGON_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.LJ_DIAMOND_BUY_ONE_DRAGON_COUNT, value);
                break;
            case UserDataEnum.NEXTTIME_Tili:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_NEXT_TIME_TILI, value);
                break;
            case UserDataEnum.EXCHANGE_ARENA_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_EXCHANGE_ARENA_COUNT, value);
                break;
            case UserDataEnum.NEXT_TIME_JOIN_GANG:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_NEXT_TIME_JOIN_GANG, value);
                break;
            case UserDataEnum.CAN_TOUCH_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_CAN_TOUCH_COUNT, value);
                break;
            case UserDataEnum.YEAR_CARD:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_YEAR_CARD, value);
                break;
            case UserDataEnum.MONTH_CARD:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_MONTH_CARD, value);
                break;
            case UserDataEnum.YEAR_CARD_END_TIME:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_YEAR_CARD_END_TIME, value);
                break;
            case UserDataEnum.MONTH_CARD_END_TIME:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_MONTH_CARD_END_TIME, value);
                break;
            case UserDataEnum.MONEY_TREE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_MONEY_TREE_COUNT, value);
                break;
            case UserDataEnum.MONEY_TREE_NEXT_TIME:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_MONEY_TREE_NEXT_TIME, value);
                break;
            case UserDataEnum.NORMAL_RESET_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_NORMAL_RESET_COUNT, value);
                break;
            case UserDataEnum.DAILY_LIVENESS:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_DAILY_LIVENESS, value);
                break;
            case UserDataEnum.LIVENESS:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LIVENESS, value);
                break;
            case UserDataEnum.LJ_TALENT_UP_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_TALENT_UP_COUNT, value);
                break;
            case UserDataEnum.LJ_DRAGON_EGG_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_DRAGON_EGG_COUNT, value);
                break;
            case UserDataEnum.LJ_HATCH_DRAGON_STONE:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_HATCH_DRAGON_STONE, value);
                break;
            case UserDataEnum.LJ_LIVENESS:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_LIVENESS, value);
                break;
            case UserDataEnum.LJ_DRAGON_STONE_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_DRAGON_STONE_COUNT, value);
                break;
            case UserDataEnum.LJ_DRAGON_UP_COUNT:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_LJ_DRAGON_UP_COUNT, value);
                break;
            case UserDataEnum.RED_NESS_MONEY:
                    AllSql.userSql.update(userID, AllSql.userSql.FIELD_RED_NESS_MONEY, value);
                break;
            case UserDataEnum.ELITE_BUY_COUNT:
                    AllSql.userSql.update(userID,AllSql.userSql.FIELD_ELITE_BUY_COUNT,value);
                break;
            case UserDataEnum.MAX_WORLD_BOSS_HURT:
                    AllSql.userSql.update(userID,AllSql.userSql.FIELD_MAX_WORLD_BOSS_HURT,value);
                break;
            case UserDataEnum.LJ_WORLD_BOSS_HURT:
                    AllSql.userSql.update(userID,AllSql.userSql.FIELD_LJ_WORLD_BOSS_HURT,value);
                break;
            case UserDataEnum.OPEN_EQUIP_BAG_COUNT:
                    AllSql.userSql.update(userID,AllSql.userSql.FIELD_OPEN_EQUIP_BAG_COUNT,value);
                break;
            case UserDataEnum.EQUIP_BAG_COUNT:
                    AllSql.userSql.update(userID,AllSql.userSql.FIELD_EQUIP_BAG_COUNT,value);
                break;
            case UserDataEnum.MR_MINE_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_MR_MINE_COUNT,value);
                break;
            case UserDataEnum.MR_EXPLOIT_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_MR_EXPLOIT_COUNT,value);
                break;
            case UserDataEnum.MR_SEARCH_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_MR_SEARCH_COUNT,value);
                break;
            case UserDataEnum.MR_FREE_RECRUIT_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_MR_FREE_RECRUIT_COUNT,value);
                break;
            case UserDataEnum.MR_LOOT_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_MR_LOOT_COUNT,value);
                break;
            case UserDataEnum.LotteryPropFreeCount:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LOTTERY_PROP_FREE_COUNT,value);
                break;
            case UserDataEnum.LotteryStoneCount:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LOTTERY_STONE_COUNT,value);
                break;
            case UserDataEnum.LotteryDragonCount:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LOTTERY_DRAGON_COUNT,value);
                break;
            case UserDataEnum.LotteryStoneNextTime:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LOTTERY_STONE_NEXTTIME,value);
                break;
            case UserDataEnum.LotteryDragonNextTime:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LOTTERY_DRAGON_NEXTTIME,value);
                break;
            case UserDataEnum.SNATCH_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_SNATCH_FIGHT_COUNT,value);
                break;
            case UserDataEnum.SNATCH_BUY_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_NEXTTIME_SNATCH,value);
                break;
            case UserDataEnum.TALENT_EXP:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_TALENT_EXP,value);
                break;
            case UserDataEnum.LJ_LOTTERY_COUNT:
                AllSql.userSql.update(userID,AllSql.userSql.FIELD_LJ_LOTTERY_COUNT,value);
                break;
        }
    }

    public int addUserData(short type, int value) {
        return addUserData(type, value,false);
    }

    public int addUserData(short type, int value, boolean sendRspd) {
        return addUserData(type, value,sendRspd,0);
    }

    public int addUserData(short type, int value, boolean sendRspd,int reasonType) {
        if (value <= 0) return userdata[type];
        if (type == UserDataEnum.LEVEL) {
            UserUpdateBaseVo extraBaseVo = Model.UserUpdateBaseMap.get(1);

            if (extraBaseVo != null && userdata[type] + value > extraBaseVo.max) value = extraBaseVo.max;
        }
        return setUserData(type, userdata[type] + value, sendRspd,reasonType);

    }

    public boolean costUserDataAndProp(int baseID, int value, boolean sendRspd,int reasonType) {
        return costUserDataAndProp(baseID, value, sendRspd,reasonType, null);
    }

    public boolean costUserDataAndProp(int baseID, int value, boolean sendRspd,int reasonType, HashSet<Long> stoneTempIDList) {
        if (enoughForcostUserDataAndProp(baseID, value) == false) return false;
        if (baseID < 1000) {
            if(baseID < 200){
                switch (baseID) {
                    case UserDataEnum.TILIZHI:
                        flushDataTime(UserDataEnum.TILIZHI, UserDataEnum.NEXTTIME_Tili, true);
                        stopRegainOnCost(UserDataEnum.TILIZHI, UserDataEnum.NEXTTIME_Tili, userdata[baseID] - value, true);
                        break;
                }
                if (baseID == UserDataEnum.MONEY) {
                    addUserData(UserDataEnum.LJ_COST_MONEY_COUNT, value, true);
                } else if (baseID == UserDataEnum.DIAMOND) {
                    addUserData(UserDataEnum.LJ_COST_DIAMOND_COUNT, value, true);
                }
                setUserData((byte) baseID, userdata[baseID] - value, sendRspd,reasonType);
            }else if(baseID < 300){
                GangVo gangVo = cacheUserVo.gang.gangVo;
                if(gangVo == null)return false;
                GangUserVo gangUserVo = gangVo.users.get(guid);
                if(gangUserVo == null)return false;
                gangUserVo.setGangUserData((byte)(baseID-200),gangUserVo.userdata[baseID-200] - value,sendRspd);
            }
        }else {

            long tempID = propModel.deleteBag(baseID, value, sendRspd);
            PropLogVo propLogVo = new PropLogVo();
            propLogVo.userID = guid;
            propLogVo.propID = baseID;
            propLogVo.count = -value;
            propLogVo.rensonType = reasonType;
            propLogVo.createTime = JkTools.getGameServerTime(null);
            AllSql.propLogSql.insertNew(propLogVo);
            if (stoneTempIDList != null) stoneTempIDList.add(tempID);

        }
        return true;
    }

    public boolean enoughForcostUserDataAndProp(int baseID, int value) {
        if (value < 0) return false;
        if (value == 0) return true;
        if (baseID < 200) {
            return userdata[baseID] >= value;
        }else if(baseID < 300){
            GangVo gangVo = cacheUserVo.gang.gangVo;
            if(gangVo == null)return false;
            GangUserVo gangUserVo = gangVo.users.get(guid);
            if(gangUserVo == null)return false;
            return gangUserVo.userdata[baseID-200] >= value;
        }

        for (PropPVo pvo : propModel.bagItems.values()) {
            if (pvo.baseID != baseID) continue;

            return pvo.count >= value;
        }

        return false;
    }

    public void updateZDL() {
        int oldZDL = zdl;
        zdl = calculateZDL();
        if (oldZDL != zdl) {
            if (cacheUserVo.rankVo != null) {
                int orderIndex = cacheUserVo.rankVo.orderIndex;
                System.out.println("========" + oldZDL + "========" + zdl + "=========");
                RankModel.rankZdlList.sortItem(cacheUserVo.rankVo, zdl);
                if (cacheUserVo.rankVo.orderIndex < 10 && cacheUserVo.rankVo.orderIndex < orderIndex) {
                    rankAiring(RankEnum.ZDL);
                }
            }
            if (cacheUserVo.gang.gangVo != null) {
                GangVo gangVo = cacheUserVo.gang.gangVo;
                gangVo.zdl += zdl - oldZDL;
                gangVo.rankGangUserList.sortItem(cacheUserVo.rankGangUserVo, RankGangUserVo.calculate(zdl,cacheUserVo.rankGangUserVo.office));
            }
            cacheUserVo.zdl = zdl;
            new HeroAttributeRspd(client,JkTools.intArrayAsList(heroAttributes));
            AllSql.userSql.update(this, AllSql.userSql.FIELD_ZDL, zdl);
        }
    }

    public void updataRedness(int intNewValue){
        if(cacheUserVo.rankRednessVo == null){
            RankModel.rankRednessList.addEnd(new RankRednessVo(cacheUserVo));
        }
        int newScore = cacheUserVo.rankRednessVo.weekRednessMoney + intNewValue;
        AllSql.userSql.update(this, AllSql.userSql.FIELD_WEEK_REDNESS_MONEY, cacheUserVo.rankRednessVo.weekRednessMoney);
        if (cacheUserVo.rankRednessVo != null) {
            RankModel.rankRednessList.sortItem(cacheUserVo.rankRednessVo, newScore);
            cacheUserVo.rankRednessVo.weekRednessMoney = newScore;
        }
    }

    public int calculateZDL() {
        int old = 0;
        int[] allAttributes = new int[AttributeEnum.MAX];
        int zdl = 0;
        addAttribute(baseVo.attributes, allAttributes, null);

        int[] talentArr = new int[AttributeEnum.MAX];
//add talent
        for (TalentPVo item : cacheUserVo.skillModel.talents.values()) {
            if (item.level > 0) {
                int[] effects = Model.TalentBaseMap.get((int) item.baseID).get(item.level).effect;
                addAttribute(effects, allAttributes, null);
                addAttribute(effects,talentArr,null);
            }
        }

        float[] equipArr = new float[AttributeEnum.MAX];
        for (PropPVo item : cacheUserVo.equipItems) {
            if (item == null) continue;
            float[] propAttribute = new float[AttributeEnum.MAX];
            PropBaseVo basePropVo = Model.PropBaseMap.get(item.baseID);
            addAttribute(basePropVo.attributes, propAttribute, (ArrayList<Byte>) item.attributeRnd);
            addAttribute(basePropVo.attributes, equipArr, (ArrayList<Byte>) item.attributeRnd);

            if (basePropVo.extraAttributes != null && item.exAttribute != null){
                int i=0;
                for(int value : item.exAttribute){
                    equipArr[basePropVo.extraAttributes[2*i]] += value;
                    i++;
                }
            }


            if (item.intensify > 0) {
                EquipIntensifyUpBaseVo upBaseVo = Model.EquipIntensifyUpBaseMap.get((int) item.intensify);
                if (upBaseVo != null) {
                    for (int i = 0; i < upBaseVo.addAttrPercent.length; i = i + 2) {
                        int attrID = upBaseVo.addAttrPercent[i];
                        equipArr[attrID] += propAttribute[attrID] * (upBaseVo.addAttrPercent[i + 1]) / 100f;
                    }
                }
            }
            if (item.advance > 0) {
                EquipAdvanceBaseVo advanceBaseVo = Model.EquipAdvanceBaseMap.get((int) item.advance);
                if (advanceBaseVo != null) {
                    for (int i = 0; i < advanceBaseVo.addAttrPercent.length; i = i + 2) {
                        int attrID = advanceBaseVo.addAttrPercent[i];
                        equipArr[attrID] += propAttribute[attrID] * ((advanceBaseVo.addAttrPercent[i + 1])) / 100f;
                    }
                }
            }

            if(item.purify > 0){
                EquipPurifyBaseVo purifyBaseVo = Model.EquipPurifyBaseMap.get((int) item.purify);
                if (purifyBaseVo != null) {
                    for (int i = 0; i < purifyBaseVo.addAttrPercent.length; i = i + 2) {
                        int attrID = purifyBaseVo.addAttrPercent[i];
                        equipArr[attrID] += propAttribute[attrID] * ((purifyBaseVo.addAttrPercent[i + 1])) / 100f;
                    }
                }
            }

            addAttribute(equipArr,allAttributes);
            equipArr = new float[AttributeEnum.MAX];

        }

        int[] attributes = new int[AttributeEnum.MAX];
        if (functionSet.contains(ModuleUIEnum.BAG_STAR) || isOpen(ModuleUIEnum.BAG_STAR)) {
            for (int i = 0; i < Model.BagBaseMap.size() && i < getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT); i++) {
                BagBaseVo bagBaseVo = Model.BagBaseMap.get(i + 1);
                if (bagBaseVo == null) {
                    break;
                }
                String[] singleRoleAttr = bagBaseVo.attrValues.split("\\|");
                for (int j = 0; j < singleRoleAttr.length; j += 2) {
                    if (String.valueOf(baseID).equals(singleRoleAttr[j])) {
                        String[] str = singleRoleAttr[j + 1].split("_");
                        for (int z = 0; z < str.length; z += 2) {
                            attributes[Integer.parseInt(str[z])] += Integer.parseInt(str[z + 1]);
                        }
                    }
                }
            }
            BagBaseVo bagBaseVo = Model.BagBaseMap.get(getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT));
            for (int i = 0; i < AttributeEnum.MAX; i++) {
                if (attributes[i] <= 0) continue;
                attributes[i] += (int) (attributes[i] * bagBaseVo.addPercent / 100f);

            }

        }

        //等级提升属性加成
        RoleLevelBaseVo rolelevelVo = Model.RoleLevelBaseMap.get(getUserData(UserDataEnum.LEVEL));
        String[] singleRoleAttr = rolelevelVo.professionAttributes.split("\\|");
        for (int i = 0; i < singleRoleAttr.length; i += 2) {
            if (String.valueOf(baseID).equals(singleRoleAttr[i])) {
                String[] str = singleRoleAttr[i + 1].split("_");
                for (int j = 0; j < str.length; j += 2) {
                    allAttributes[Integer.parseInt(str[j])] += Integer.parseInt(str[j + 1]);
                }
            }
        }



//vip战力计算
        if (client.passportVo.vip > 0) {
            VipBaseVo vipBase = Model.VipBaseMap.get((int) client.passportVo.vip);
            for (int i = 0; i < vipBase.attribute.length; i += 2) {
                allAttributes[vipBase.attribute[i]] += vipBase.attribute[i + 1];
            }
        }

//        //皮肤战力计算
//        if (cacheUserVo.skin > 0 && Model.SkinBaseMap.get((int) cacheUserVo.skin).attributes != null) {
//            SkinBaseVo skinBase = Model.SkinBaseMap.get((int) cacheUserVo.skin);
//            for (int i = 0; i < skinBase.attributes.length; i += 2) {
//                allAttributes[skinBase.attributes[i]] += skinBase.attributes[i + 1];
//            }
//        }


        //技能战力计算
      for (int id : baseVo.skills)
        {
            ArrayList<SkillBaseVo> list = Model.SkillBaseMap.get(id);
            SkillPVo skillPVo = cacheUserVo.skillModel.skills.get(id);
            int index = 0;
            if(list.get(0).type == 0){
                if(skillPVo != null){
                    index = skillPVo.level-1;
                }
            }else{
                if(skillPVo != null){
                    index = skillPVo.level;
                }
            }
            SkillBaseVo sklBase = list.get(index);
            if (sklBase.fc > 0)
            {
                zdl += sklBase.fc;
            }
        }


        for(short achieveID : dragonModel.dragonAchieveSet){
            DragonAchieveBaseVo baseVo = Model.DragonAchieveBaseMap.get((int)achieveID);
            if(baseVo == null)continue;
            addAttribute(baseVo.addAttribute,allAttributes,null);
        }
        int[] dragonAttribute = new int[AttributeEnum.MAX];
        for(DragonPVo dragonPVo : cacheUserVo.dragonCacheModel.dragonsMap.values()){
            if(!dragonPVo.isActive)continue;
            int[] dragonAttr = new int[AttributeEnum.MAX];
            DragonLevelBaseVo vo = Model.DragonLevelBaseMap.get((int)dragonPVo.level);
            ArrayList<DragonAdvanceBaseVo> list = Model.DragonAdvanceBaseMap.get((int)dragonPVo.baseID);
            DragonAdvanceBaseVo advanceBaseVo = list.get(dragonPVo.advance);
            for (int i = 0; i < vo.attribute.length; i += 2) {
                dragonAttr[vo.attribute[i]] = vo.attribute[i + 1] * advanceBaseVo.percent/100;
            }
            JkTools.arrayAdd(dragonAttr,dragonAttribute);
        }
        JkTools.arrayAdd(dragonAttribute,allAttributes);
//        if(cacheUserVo.dragonCacheModel.mainDragon != null){
//            int[] arr = dragonModel.getDragonAttr(cacheUserVo.dragonCacheModel.mainDragon);
//            if(arr != null)JkTools.arrayAdd(arr,allAttributes);
//            System.out.println("===========DragonZDL :"+getArrZDL(arr));
//        }

        for(WingCorePVo corePVo : cacheUserVo.wingModel.coreMap.values()){
            if(corePVo.level > 0){
                ArrayList<WingCoreUpBaseVo> list = Model.WingCoreUpBaseMap.get((int)corePVo.id);
                WingCoreUpBaseVo wingCoreUpBaseVo = list.get(corePVo.level-1);
                addAttribute(wingCoreUpBaseVo.addAttr,allAttributes,null);
            }
            ArrayList<WingCoreAdvanceBaseVo> list = Model.WingCoreAdvanceBaseMap.get((int)corePVo.id);
            WingCoreAdvanceBaseVo wingCoreAdvanceBaseVo = list.get(corePVo.advance);
            addAttribute(wingCoreAdvanceBaseVo.addAttributes,allAttributes,null);
        }

        if(cacheUserVo.wingModel.wingSet.size() > 0){
            for(short id : cacheUserVo.wingModel.wingSet){
                WingBaseVo wingBaseVo = Model.WingBaseMap.get((int)id);
                addAttribute(wingBaseVo.attribute,allAttributes,null);
            }
        }


        HashMap<Integer,int[]> partAttr = new HashMap<>();
        HashMap<Integer,HashMap<Integer,Integer>> partSuit= new HashMap<>();
        HashMap<Integer,Integer> partPercent = new HashMap<>();
        for (StonePVo stonePVo : cacheUserVo.equipDragonStone.values()) {
            PropBaseVo basePropVo = Model.PropBaseMap.get(stonePVo.baseID);
            int part = stonePVo.index / 5;
            if(cacheUserVo.equipItems[part+1] == null)continue;
            if(partAttr.containsKey(part)){
                addAttribute(basePropVo.attributes, partAttr.get(part),null);
            }else{
                int[] attr = new int[AttributeEnum.MAX];
                addAttribute(basePropVo.attributes, attr,null);
                partAttr.put(part,attr);
            }
            int suit = basePropVo.suit;
            if(suit == 0){
                partPercent.put(part,basePropVo.effect[1]);
                continue;
            }
            if(partSuit.containsKey(part)){
                HashMap<Integer,Integer> suitMap = partSuit.get(part);
                if(suitMap.containsKey(suit)){
                    suitMap.put(suit,suitMap.get(suit)+1);
                }else{
                    suitMap.put(suit,1);
                }
            }else{
                HashMap<Integer,Integer> suitMap = new HashMap<>();
                suitMap.put(suit,1);
                partSuit.put(part,suitMap);
            }
        }
        int[] propAttribute = new int[AttributeEnum.MAX];
        for(int part : partAttr.keySet()){
            int[] propAttr = new int[AttributeEnum.MAX];
            JkTools.arrayAdd(partAttr.get(part),propAttr);
            if(partSuit.containsKey(part)){
                HashMap<Integer,Integer> suitMap = partSuit.get(part);
                for(Map.Entry<Integer,Integer> item: suitMap.entrySet()){
                    if(item.getValue()<=1)continue;
                    int[] attr = PropSuitModel.getSuitAddAttr(item.getKey(),item.getValue());
                    if(attr == null)continue;
                    addAttribute(attr, propAttr,null);
                }
            }
            if(partPercent.containsKey(part)){
                int percent = partPercent.get(part);
                for (int i = 0; i < AttributeEnum.MAX; i++) {
                    if (propAttr[i] <= 0) continue;
                    propAttr[i] += (int) (propAttr[i] * percent / 100f);
                }
            }
            JkTools.arrayAdd(propAttr,propAttribute);
        }

        System.out.println("====propAttribute==="+getArrZDL(propAttribute));
        JkTools.arrayAdd(attributes,allAttributes);
        JkTools.arrayAdd(propAttribute,allAttributes);

        for(HeroTagVo heroTagVo : heroTagModel.zdlTagMap.values()){
            HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get((int)heroTagVo.tagID);
            addAttribute(heroTagBaseVo.attribute,allAttributes,null);
        }

        for(HeroTagVo heroTagVo : heroTagModel.activeTagMap.values()){
            if(heroTagVo.status == 1)continue;
            HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get((int)heroTagVo.tagID);
            addAttribute(heroTagBaseVo.attribute,allAttributes,null);
        }

        for(EmperorPVo emperorPVo : emperorModel.emperorMap.values()){
            int[] attribute = new int[AttributeEnum.MAX];
            if(emperorPVo.advance == 0)continue;
            EmperorBaseVo emperorBaseVo = Model.EmperorBaseMap.get((int)emperorPVo.id);
            if(emperorBaseVo == null)continue;
            addAttribute(emperorBaseVo.initialAttributes,attribute,null);
            ArrayList<EmperorAdvanceBaseVo> list = Model.EmperorAdvanceBaseMap.get((int)emperorPVo.id);
            if(list == null)continue;
            EmperorAdvanceBaseVo emperorAdvanceBaseVo = list.get(emperorPVo.advance);
            if(emperorAdvanceBaseVo == null)continue;
            addAttribute(emperorAdvanceBaseVo.attributeAddition,attribute,null);
            if(emperorPVo.level>0){
                EmperorLevelBaseVo emperorLevelBaseVo = Model.EmperorLevelBaseMap.get((int)emperorPVo.level);
                if(emperorLevelBaseVo == null)continue;
                addAttribute(emperorLevelBaseVo.properties,attribute,null);
            }
            if(emperorPVo.up>0){
                EmperorLevelUpBaseVo emperorLevelUpBaseVo = Model.EmperorLevelUpBaseMap.get((int)emperorPVo.up);
                if(emperorLevelUpBaseVo == null)continue;
                addAttribute(emperorLevelUpBaseVo.addAttribute,attribute,null);
            }
            for(int propID : emperorPVo.materials){
                PropBaseVo propBaseVo = Model.PropBaseMap.get(propID);
                if(propBaseVo == null)continue;
                addAttribute(propBaseVo.attributes,attribute,null);
            }
            ArrayList<EmperorQualityBaseVo> qualityList = Model.EmperorQualityBaseMap.get((int)emperorPVo.id);
            if(qualityList == null)continue;
            EmperorQualityBaseVo emperorQualityBaseVo = qualityList.get(emperorPVo.quality);
            if(emperorQualityBaseVo == null)continue;
            addAttributeByPercent(attribute,attribute,emperorQualityBaseVo.percent);
            for(int propID : emperorPVo.equip){
                PropBaseVo propBaseVo = Model.PropBaseMap.get(propID);
                if(propBaseVo == null)continue;
                addAttribute(propBaseVo.attributes,attribute,null);
            }
            for(int i=0;i<emperorBaseVo.skill.length;i++){
                ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(emperorBaseVo.skill[i]);
                int level = (int)((ArrayList<Byte>)emperorPVo.skill).get(i);
                int index = 0;
                if (skillList.get(0).type == 0) {
                    index = level - 1;
                } else {
                    index = level;
                }
                SkillBaseVo sklBase = skillList.get(index);
                if (sklBase.fc > 0) {
                    zdl += sklBase.fc;
                }
            }
            for(int i=0;i<emperorBaseVo.talent.length;i++){
                ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(emperorBaseVo.talent[i]);
                int level = (int)((ArrayList<Byte>)emperorPVo.talent).get(i);
                int index = 0;
                if (skillList.get(0).type == 0) {
                    index = level - 1;
                } else {
                    index = level;
                }
                SkillBaseVo sklBase = skillList.get(index);
                if (sklBase.fc > 0) {
                    zdl += sklBase.fc;
                }
            }
            ArrayList<EmperorSealBaseVo> sealList = Model.EmperorSealBaseMap.get((int)emperorPVo.id);
            if(sealList == null)continue;
            if(emperorPVo.seal > 0){
                EmperorSealBaseVo emperorSealBaseVo = sealList.get(emperorPVo.seal-1);
                if(emperorSealBaseVo == null)continue;
                addAttribute(emperorSealBaseVo.attribute,attribute,null);
            }
            EmperorAttributePVo emperorAttributePVo = new EmperorAttributePVo();
            emperorAttributePVo.id = emperorPVo.id;
            emperorAttributePVo.attribute = JkTools.intArrayAsList(attribute);
            emperorModel.attributeMap.put(emperorPVo.id,emperorAttributePVo);
            JkTools.arrayAdd(attributes,allAttributes);
        }
        for(AcheivePVo acheivePVo : emperorModel.achieveMap.values()){
            ArrayList<EmperorAchieveBaseVo> achieveList = Model.EmperorAchieveBaseMap.get((int)acheivePVo.achieveID);
            if(achieveList == null)continue;
            if(acheivePVo.level >0){
                EmperorAchieveBaseVo emperorAchieveBaseVo = achieveList.get(acheivePVo.level-1);
                if(emperorAchieveBaseVo == null)continue;
                int[] precent = emperorAchieveBaseVo.precent;
                EmperorAttributePVo emperorAttributePVo = emperorModel.attributeMap.get((byte)emperorAchieveBaseVo.emperor);
                if(emperorAttributePVo == null)continue;
                int[] attribute = JkTools.List2IntArray(emperorAttributePVo.attribute);
                for(int i=0;i<precent.length;i+=2){
                    attribute[precent[i]] += attribute[precent[i]]*precent[i+1]/100;
                }
                JkTools.arrayAdd(attributes,allAttributes);
            }
        }

        for(RealmVo realmVo : realmModel.realmMap.values()){
            RealmDanBaseVo realmDanBaseVo = Model.RealmDanBaseMap.get((int)realmVo.realm).get(realmVo.dan-1);
            if(realmDanBaseVo == null)continue;
            addAttribute(realmDanBaseVo.addAttribute,allAttributes,null);
            RealmCycloneBaseVo realmCycloneBaseVo = Model.RealmCycloneBaseMap.get((int)realmVo.realm).get((realmVo.dan-1)*RealmModel.MAX_CYCLONE+realmVo.cyclone-1);
            if(realmCycloneBaseVo == null)continue;
            addAttribute(realmCycloneBaseVo.addAttribute,allAttributes,null);
            ArrayList<RealmCycloneUpBaseVo> list = Model.RealmCycloneUpBaseMap.get((int)realmVo.realm);
            if(list == null)continue;
            int base = (realmVo.cycloneUpDan-1)*RealmModel.MAX_CYCLONE;
            for(int i=0;i<base;i++){
                RealmCycloneUpBaseVo realmCycloneUpBaseVo = list.get(i);
                if(realmCycloneUpBaseVo == null)continue;
                addAttribute(realmCycloneUpBaseVo.addAttribute,allAttributes,null);
            }
            for(byte cyclone : realmVo.cycloneUp){
                RealmCycloneUpBaseVo realmCycloneUpBaseVo = list.get(base+cyclone-1);
                if(realmCycloneUpBaseVo == null)continue;
                addAttribute(realmCycloneUpBaseVo.addAttribute,allAttributes,null);
            }
            ArrayList<RealmDanGasBaseVo> gasList = Model.RealmDanGasBaseMap.get((int)realmVo.realm);
            ArrayList<RealmDanSoulBaseVo> soulList = Model.RealmDanSoulBaseMap.get((int)realmVo.realm);
            for(DanPVo danPVo : realmVo.danMap.values()){
                addAttribute(gasList.get(danPVo.gasStar).attributeAddition, allAttributes, null);
                addAttribute(soulList.get(danPVo.gasStar).attributeAddition, allAttributes, null);
            }
        }

        //公会技能加成
        int[] gangArr = new int[AttributeEnum.MAX];
        if (cacheUserVo.gang.gangVo != null && gangSkillModel.gangSkillList.size() > 0) {
            allAttributes[AttributeEnum.ATTACK] += allAttributes[AttributeEnum.ATTACK] * gangSkillModel.gangSkillMap.get(GangSkillEnum.ATTACK).effect / 1000f;
            allAttributes[AttributeEnum.MATTACK] += allAttributes[AttributeEnum.MATTACK] * gangSkillModel.gangSkillMap.get(GangSkillEnum.ATTACK).effect / 1000f;
            allAttributes[AttributeEnum.DEFENCE] += allAttributes[AttributeEnum.DEFENCE] * gangSkillModel.gangSkillMap.get(GangSkillEnum.DEFENCE).effect / 1000f;
            allAttributes[AttributeEnum.MDEFENCE] += allAttributes[AttributeEnum.MDEFENCE] * gangSkillModel.gangSkillMap.get(GangSkillEnum.DEFENCE).effect / 1000f;
            allAttributes[AttributeEnum.HP_MAX] += allAttributes[AttributeEnum.HP_MAX] * gangSkillModel.gangSkillMap.get(GangSkillEnum.HP).effect / 1000f;
            gangArr[AttributeEnum.ATTACK] += allAttributes[AttributeEnum.ATTACK] * gangSkillModel.gangSkillMap.get(GangSkillEnum.ATTACK).effect / 1000f;
            gangArr[AttributeEnum.MATTACK] += allAttributes[AttributeEnum.MATTACK] * gangSkillModel.gangSkillMap.get(GangSkillEnum.ATTACK).effect / 1000f;
            gangArr[AttributeEnum.DEFENCE] += allAttributes[AttributeEnum.DEFENCE] * gangSkillModel.gangSkillMap.get(GangSkillEnum.DEFENCE).effect / 1000f;
            gangArr[AttributeEnum.MDEFENCE] += allAttributes[AttributeEnum.MDEFENCE] * gangSkillModel.gangSkillMap.get(GangSkillEnum.DEFENCE).effect / 1000f;
            gangArr[AttributeEnum.HP_MAX] += allAttributes[AttributeEnum.HP_MAX] * gangSkillModel.gangSkillMap.get(GangSkillEnum.HP).effect / 1000f;
        }

        zdl += getArrZDL(allAttributes);
        cacheUserVo.attributeModel.attribute = allAttributes;

        dragonStoneZDL = getArrZDL(propAttribute);
        System.out.println("=============Total:" + zdl + "===============");
        heroAttributes=allAttributes;

        return zdl;
    }

    public int getExZDL(PropPVo propPVo,PropBaseVo propBaseVo){
        int[] attr = new int[AttributeEnum.MAX];
        int i = 0;
        for(int value:propPVo.exAttribute){
            attr[propBaseVo.extraAttributes[2*i]] = value;
            i++;
        }
        return getArrZDL(attr);
    }

    public int getArrZDL(int[] attribute){
        int zdl = 0;
        for (int i = 0; i < AttributeEnum.MAX; i++) {
            if(attribute[i] <= 0)continue;
            if (Model.CommZdlBaseMap.containsKey(i)) {
                zdl += (Model.CommZdlBaseMap.get(i).zdl * attribute[i] + 50) / 100;
            }
        }
        return zdl;
    }

    public float getArrZDL(float[] attribute){
        float zdl = 0;
        for (int i = 0; i < AttributeEnum.MAX; i++) {
            if(attribute[i] <= 0)continue;
            if (Model.CommZdlBaseMap.containsKey(i)) {
                zdl += Model.CommZdlBaseMap.get(i).zdl * attribute[i] / 100f;
            }
        }
        return Math.round(zdl);
    }

    public void addAttribute(float[] atts,int[] des){
        for(int i=0;i<atts.length;i++){
            des[i] += Math.round(atts[i]);
        }
    }

    public void addAttributeByPercent(int[] atts, int[] des,int percent){
        if(atts == null)return;
        for (int i = 0; i < atts.length; i += 2) {
            des[i] += atts[i] * percent/100;
        }
    }

    public void addAttribute(int[] atts, int[] des, ArrayList<Byte> attributeRnd) {
        if(atts == null)return;
        if (attributeRnd == null || attributeRnd.size() == 0) {
            for (int i = 0, len = atts.length; i < len; i += 2) {
                des[atts[i]] += atts[i + 1];
            }
        } else {
            for (int i = 0, len = atts.length; i < len; i += 2) {
                if (attributeRnd.get(i / 2) > 0) {
                    des[atts[i]] += atts[i + 1] * (100 + attributeRnd.get(i / 2)) / 100;
                }
            }
        }
    }

    public void addAttribute(int[] atts, float[] des, ArrayList<Byte> attributeRnd) {
        if(atts == null)return;
        if (attributeRnd == null || attributeRnd.size() == 0) {
            for (int i = 0, len = atts.length; i < len; i += 2) {
                des[atts[i]] += atts[i + 1];
            }
        } else {
            for (int i = 0, len = atts.length; i < len; i += 2) {
                if (attributeRnd.get(i / 2) >= 0) {
                    des[atts[i]] += atts[i + 1] * (100 + attributeRnd.get(i / 2)) / 100f;
                }
            }
        }
    }

    public boolean isCDClear(Client client, short dataType) {
        return JkTools.getGameServerTime(client) >= getUserData(dataType);
    }

    public void addExpInGaming(int expMore) {
        if (expMore <= 0) return;
        int level = getUserData(UserDataEnum.LEVEL);
        //满级不加
        if (Model.RoleLevelBaseMap.containsKey(level + 1) == false) return;
        int exp = addUserData(UserDataEnum.EXP, expMore);
        // DCGameLog.getInstance().AddLog(GameLogType.COINGAIN, new String[]{guid + "", UserDataEnum.EXP + "", (expMore)  + "", exp + "", "addExpGaming"});


        //判断升级

        int allCostExp = 0;
        int allAddLvl = 0;

        while (true) {
            level = getUserData(UserDataEnum.LEVEL);
            RoleLevelBaseVo levelVo = Model.RoleLevelBaseMap.get(level);
            if (levelVo == null) return;
            if (costUserDataAndProp(UserDataEnum.EXP, levelVo.nextExp, false,ReasonTypeEnum.LEVEL_UP) == false) break;
            allCostExp += levelVo.nextExp;
            allAddLvl++;
            addUserData(UserDataEnum.LEVEL, 1, true);
            propModel.addListToBag(Model.RoleLevelBaseMap.get(level + 1).reward,ReasonTypeEnum.LEVEL_UP);
            //满级经验变0
            if (Model.RoleLevelBaseMap.containsKey(level + 2) == false) {
                setUserData(UserDataEnum.EXP, 0);
                break;
            }
            ;

        }
        if (allAddLvl > 0) {
            if (missionModel.hasAcceptedMission(MissionTypeEnum.MAIN) == false) {
                missionModel.calculateNewMission();
            }
            //   DCGameLog.getInstance().AddLog(GameLogType.COINLOST, new String[]{guid + "",UserDataEnum.EXP + "", (allCostExp)  + "", getUserData(UserDataEnum.EXP) + "", "addExpGaming"});
            //     DCGameLog.getInstance().AddLog(GameLogType.COINGAIN, new String[]{guid + "", UserDataEnum.LEVEL + "", (allAddLvl)  + "", getUserData(UserDataEnum.LEVEL)  + "", "addExpGaming"});

        }
        setUserData(UserDataEnum.EXP, getUserData(UserDataEnum.EXP), true);
        //   new UpdateUserDataRspd(client, UserDataEnum.EXP, getUserData(UserDataEnum.EXP));
    }


    public void traceBug(String id) {
        if (!isDebug()) return;
        System.out.println("----" + id + "---");
        for (PropPVo vo : fightingLoot.totalProps) {
            System.out.println(vo.tempID);
        }

    }

    public boolean isDebug() {
        return guid == 22800437;
    }

    public static User createOne(Client client, byte baseID) {
        if (client.passportVo.userMap.containsKey(baseID)) return null;
        User us = new User(client);
        us.baseID = baseID;
        us.setUserData(UserDataEnum.RoleCreatTime, JkTools.getGameServerTime(client));
        if (!AllSql.userSql.insertNew(us)) {
            return null;
        }
        us.initGame();
        CacheUserVo cuVo = us.cacheUserVo = new CacheUserVo();
        client.passportVo.userMap.put(baseID, cuVo);
        cuVo.passportVo = client.passportVo;
        cuVo.guid = us.guid;
        cuVo.baseID = us.baseID;
        cuVo.level = 1;
        cuVo.zdl = 0;
        client.passportVo.userMap.put(cuVo.baseID, cuVo);

        us.initWithCache();
        CacheUserVo.allMap.put(cuVo.guid, cuVo);
        us.cacheUserVo.portrait = (byte)us.baseVo.defaultSkin;
        AllSql.userSql.update(us.guid,AllSql.userSql.FIELD_PORTRAIT,us.cacheUserVo.portrait);
        return us;
    }


    public void calculateFbScore() {
        chaperScore = 0;
        for (MapQualityPVo item : mapQualityMap.values()) {
            chaperScore += item.stars.size();
        }
    }

    public boolean enoughForcostUserDataAndProp(int[] dataList) {
        if (dataList == null) return true;
        for (int i = 0, len = dataList.length; i < len; i += 2) {
            if (enoughForcostUserDataAndProp(dataList[i], dataList[i + 1]) == false) return false;

        }
        return true;
    }

    public boolean costUserDataAndPropList(int[] dataList, boolean sendRspd,int reasonType, HashSet<Long> stoneTempIDList) {
        if (dataList == null) return true;
        for (int i = 0, len = dataList.length; i < len; i += 2) {
            if (enoughForcostUserDataAndProp(dataList[i], dataList[i + 1]) == false) return false;

        }
        for (int i = 0, len = dataList.length; i < len; i += 2) {
            costUserDataAndProp(dataList[i], dataList[i + 1], sendRspd,reasonType, stoneTempIDList);
        }
        return true;

    }

    public boolean costUserDataAndPropListAutoBuy(int[] dataList, boolean sendRspd,int reasonType, HashSet<Long> stoneTempIDList) {
        if (dataList == null) return true;
        int[] dataList2 = new int[dataList.length];
        for (int i = 0; i < dataList.length; i++) {
            dataList2[i] = dataList[i];
        }
        dataList = dataList2;
        if (getUserData(UserDataEnum.DIAMOND) == 0) return costUserDataAndPropList(dataList, sendRspd,reasonType, stoneTempIDList);
        int totalDiamondCost = 0;
        for (int i = 0, len = dataList.length; i < len; i += 2) {
            int baseID = dataList[i];
            int value = dataList[i + 1];
            if (baseID > 100) {
                PropPVo hasProp = null;

                for (PropPVo pvo : propModel.bagItems.values()) {
                    if (pvo.baseID == baseID) {
                        hasProp = pvo;
                        break;
                    }
                    ;


                }
                int hasCount = 0;
                if (hasProp != null) {
                    hasCount = hasProp.count;
                    if (stoneTempIDList != null) {
                        stoneTempIDList.add(hasProp.tempID);
                    }
                }

                if (hasCount < value) {
                    dataList[i + 1] = hasCount;
                    totalDiamondCost += Model.AutoBuyBaseMap.get(baseID).diamond * (value - hasCount);
                }

            } else {
                if (baseID == UserDataEnum.DIAMOND) {
                    totalDiamondCost += value;
                    dataList[i + 1] = 0;
                }
            }


        }
        if (costUserDataAndProp(UserDataEnum.DIAMOND, totalDiamondCost, sendRspd,reasonType, null) == false) return false;

        costUserDataAndPropList(dataList, sendRspd,reasonType, stoneTempIDList);
        return true;
    }
    public int getExtraVoDailyAddMax(UserUpdateBaseVo extraVo) {
        if (extraVo.dailyAddMax >= 0) return extraVo.dailyAddMax;
        if (extraVo.dailyAddMax == -1) {
            if (extraVo.dataID == UserDataEnum.TILIZHI) {
                int max = Model.RoleLevelBaseMap.get(getUserData(UserDataEnum.LEVEL)).UserdataMax.get(extraVo.dataID);
                if (gangSkillModel.gangSkillList.size() > 0) {
                    GangSkillMsgPVo pVo = gangSkillModel.gangSkillMap.get(GangSkillEnum.TILI);
                    if (pVo != null)
                        max += pVo.effect;
                    return max;
                }
                return max;
            }
            VipBaseVo vo = Model.VipBaseMap.get((int) client.passportVo.vip);
            if (vo == null) return 0;
            switch (extraVo.dataID) {
                case UserDataEnum.WormNestResetTime:
                    return vo.duchongMapReset;
                case UserDataEnum.DRAGON_STONE_BUY_COUNT:
                    return vo.dragonStoneCount;
                case UserDataEnum.EXCHANGE_TILI_COUNT:
                    return vo.buyTili;
                case UserDataEnum.EXCHANGE_ARENA_COUNT:
                    return vo.arenaBuyCount;
                case UserDataEnum.NORMAL_RESET_COUNT:
                    return vo.commMapReset;
                case UserDataEnum.MONEY_TREE_COUNT:
                    return vo.moneytreeCount;
                case UserDataEnum.ELITE_BUY_COUNT:
                    return vo.eliteByCount;
                case UserDataEnum.LotteryPropFreeCount:
                    return vo.lotteryPropCount;
            }
        }
        if (extraVo.dailyAddMax == -2) {
            return -2;
        }
        return 0;
    }

    public int getUserVoAddMax(UserUpdateBaseVo updateVo) {
        if (updateVo.dailyAddMax >= 0) return updateVo.dailyAddMax;
        if (updateVo.dailyAddMax == -1) {
            if (updateVo.dataID == UserDataEnum.TILIZHI) {
                int max = Model.RoleLevelBaseMap.get(getUserData(UserDataEnum.LEVEL)).UserdataMax.get(updateVo.dataID);
                if (gangSkillModel.gangSkillList.size() > 0) {
                    GangSkillMsgPVo pVo = gangSkillModel.gangSkillMap.get(GangSkillEnum.TILI);
                    if (pVo != null)
                        max += pVo.effect;
                    return max;
                }
                return max;
            }
            VipBaseVo vo = Model.VipBaseMap.get((int) client.passportVo.vip);
            if (vo == null) return 0;
            switch (updateVo.dataID) {
                case UserDataEnum.WormNestResetTime:
                    return vo.duchongMapReset;
                case UserDataEnum.DRAGON_STONE_BUY_COUNT:
                    return vo.dragonStoneCount;
                case UserDataEnum.EXCHANGE_TILI_COUNT:
                    return vo.buyTili;
                case UserDataEnum.EXCHANGE_ARENA_COUNT:
                    return vo.arenaBuyCount;
                case UserDataEnum.NORMAL_RESET_COUNT:
                    return vo.commMapReset;
                case UserDataEnum.MONEY_TREE_COUNT:
                    return vo.moneytreeCount;
                case UserDataEnum.ELITE_BUY_COUNT:
                    return vo.eliteByCount;
                case UserDataEnum.LotteryPropFreeCount:
                    return vo.lotteryPropCount;
            }
        }
        if (updateVo.dailyAddMax == -2) {
            return -2;
        }
        return 0;
    }

    public int getGangUserVoAddMax(UserUpdateBaseVo updateVo) {
        if (updateVo.dailyAddMax >= 0) return updateVo.dailyAddMax;
        if (updateVo.dailyAddMax == -1) {
            VipBaseVo vo = Model.VipBaseMap.get((int) client.passportVo.vip);
            if (vo == null) return 0;
            switch (updateVo.dataID) {
                case GangUserDataEnum.DIVINATION_COUNT:
                    return vo.divinationCount;
                case GangUserDataEnum.GUESS_COUNT:
                    return vo.guessCount;
            }
        }
        if (updateVo.dailyAddMax == -2) {
            return -2;
        }
        return 0;
    }

    public void stopRegainOnCost(short dataEnum, short nextTimeEnum, int newValue, boolean sendRspd) {
        for(UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()){
            if(vo.dataType == 1 && vo.dataID == dataEnum){//userdata
                UserUpdateBaseVo extraVo = Model.UserUpdateBaseMap.get(vo.ID);
                if (getUserData(dataEnum) >= getExtraVoDailyAddMax(extraVo) && newValue < getExtraVoDailyAddMax(extraVo)) {
                    setUserData(nextTimeEnum, JkTools.getGameServerTime(client) + extraVo.getBySeconds, sendRspd);
                }
            }
        }
    }

    public void flushDataTime(short dataEnum, short nextTimeEnum, boolean sendRspd) {

        if(getUserData(nextTimeEnum)==0){
            for(UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()) {
                if (vo.dataType == 1 && vo.dataID == dataEnum) {
                    UserUpdateBaseVo extraVo = Model.UserUpdateBaseMap.get(vo.ID);
                    setUserData(nextTimeEnum, JkTools.getGameServerTime(client) + extraVo.getBySeconds, sendRspd);
                }
            }
//            setUserData(nextTimeEnum,JkTools.getGameServerTime(client));
            return;
        }
        int hasSeconds = JkTools.getGameServerTime(client) - getUserData(nextTimeEnum);
        if (hasSeconds < -5) {
            return;
        }
        for(UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()){
            if(vo.dataType == 1 && vo.dataID == dataEnum){
                UserUpdateBaseVo extraVo = Model.UserUpdateBaseMap.get(vo.ID);
                int regainMax = getExtraVoDailyAddMax(extraVo);
                if (getUserData(dataEnum) >= regainMax) {
                    return;
                }
                addUserData(dataEnum, Math.min(regainMax - getUserData(dataEnum),
                        hasSeconds / extraVo.getBySeconds + 1), sendRspd);
                setUserData(nextTimeEnum, JkTools.getGameServerTime(client) + extraVo.getBySeconds, sendRspd);
            }
        }
    }

    public int getCreatedDays() {
        int days = JkTools.getGameServerDaysWith(client, userdata[UserDataEnum.RoleCreatTime]) + 1;
        return days;
    }

    private void initBagCoolData() {
        UserUpdateBaseVo extra = Model.UserUpdateBaseMap.get(13);
        if (extra == null) {
            return;
        }
        BagBaseVo bagBaseVo = Model.BagBaseMap.get(extra.init + 1);
        if (bagBaseVo == null) {
            return;
        }
        setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, JkTools.getGameServerTime(client), true);
        setUserData(UserDataEnum.NEXTTIME_BAG_CELL, JkTools.getGameServerTime(client) + bagBaseVo.coolTime, true);
    }

    public void flushBagCellCD(int hasCount) {
        if (getUserData(UserDataEnum.NEXTTIME_BAG_CELL) == -1) {
            return;
        }
        int nowTime = JkTools.getGameServerTime(client);
        int time = nowTime - getUserData(UserDataEnum.BAG_REMOVE_CD_TIME);
        int index = getUserData(UserDataEnum.BAG_CELL_CD_COUNT);
        int num = 0;
        for (int i = index + 1; time >= 0; i++) {
            if (index + num - hasCount >= LIMIT_CD_COUNT) {
                addUserData(UserDataEnum.BAG_CELL_CD_COUNT, num, true);
                setUserData(UserDataEnum.NEXTTIME_BAG_CELL, -1, true);
                setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, -1, true);
                return;
            }
            BagBaseVo bagBaseVo = Model.BagBaseMap.get(i);
            if (bagBaseVo == null) {
                setUserData(UserDataEnum.BAG_CELL_CD_COUNT, i - 1, true);
                setUserData(UserDataEnum.NEXTTIME_BAG_CELL, -1, true);
                return;
            }
            time -= bagBaseVo.coolTime;
            num++;
        }
        if (num == 1) {
            return;
        }
        BagBaseVo bagBaseVo = Model.BagBaseMap.get(index + num);
        if (bagBaseVo == null) {
            if(index + num - 1 == Model.BagBaseMap.size()){
                setUserData(UserDataEnum.BAG_CELL_CD_COUNT, Model.BagBaseMap.size(), true);
                setUserData(UserDataEnum.NEXTTIME_BAG_CELL, -1, true);
                setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, -1, true);
            }
            return;
        }
        num--;
        addUserData(UserDataEnum.BAG_CELL_CD_COUNT, num);
        setUserData(UserDataEnum.NEXTTIME_BAG_CELL, nowTime - time);
        setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, nowTime - time - bagBaseVo.coolTime);
    }

    public void checkFunctionOpen(boolean isLogin, boolean isLevelUp) {
        boolean isUpdateZDL = false;
        if(isSendMsg(isLogin,ModuleUIEnum.BAG_STAR)){
            if(getUserData(UserDataEnum.NEXTTIME_BAG_CELL) == 0){
                initBagCoolData();
                isUpdateZDL = true;
            }
            flushBagCellCD(getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT));
        }

        if (isOpenNow(ModuleUIEnum.MONSTER_ILLUSTRATED)) {
            if (illustratedModel.monsterGroupList.size() == 0) {
                illustratedModel.init();
            }
        }
        if (isSendMsg(isLogin, ModuleUIEnum.WORLD_TREE)) {
            GetTalkBackRqst getTalkBackRqst = new GetTalkBackRqst();
            getTalkBackRqst.page = 0;
            getTalkBackRqst.type = -1;
            new GetTalkBackCmd().getTalkBack(client, this, getTalkBackRqst, false);
        }
        if (isSendMsg(isLogin, ModuleUIEnum.MAIL_VIEW)) {
            if (!mailModel.flag) {
                AllSql.mailSql.loadOneMail(this);
            }
            mailModel.createMailListRspd();
        }
        if (isSendMsg(isLogin, ModuleUIEnum.PROP_SHOP)) {
            if (propModel.propShopMap.size() == 0) {
                propModel.randomPropShopInit();
            } else {
                int time = JkTools.getGameServerTime(client) - propModel.propShopFlushCD;
                if (time >= 0) {
                    propModel.propShopFlushCD = PropModel.nextEndTime(client);
                    propModel.randomPropShop(0, true);
                } else {
                    new RandomPropShopRspd(client, propModel.propShopFlushCD, propModel.propShopMap.values());
                }
            }
        }
        DragonCacheModel dragonCacheModel = cacheUserVo.dragonCacheModel;
        if (isSendMsg(isLogin, ModuleUIEnum.DRAGON)) {
//            if(dragonModel.dragonLimitTimeMap.size()>0){
//                dragonModel.flushDragonList(client);
//                new AddDragonLimitTimeRspd(client,dragonModel.dragonLimitTimeMap.values());
//            }
            ArrayList<DragonPVo> dragonList = new ArrayList<>();
            for(DragonPVo dragonPVo : dragonCacheModel.dragonsMap.values()){
                dragonList.add(dragonPVo);
            }
            new DragonListRspd(client,dragonList);
            ArrayList<DragonTalentListPVo> list = new ArrayList<>();
            for(Map.Entry<Short,ArrayList<DragonTalentPVo>> item : dragonModel.dragonTalentMap.entrySet()){
                DragonTalentListPVo dragonTalentListPVo = new DragonTalentListPVo();
                dragonTalentListPVo.dragonID = item.getKey();
                dragonTalentListPVo.talent = item.getValue();
                list.add(dragonTalentListPVo);
            }
            new DragonTalentRspd(client,list);
            if (dragonCacheModel.mainDragon != null) {
                new DragonSelectRspd(client, dragonCacheModel.getMainDragonID());
            }
            if(dragonModel.dragonAchieveSet.size()>0){
                new DragonAchieveRspd(client,dragonModel.dragonAchieveSet);
            }
        }

        if (isSendMsg(isLogin, ModuleUIEnum.SIGN)) {
            Calendar todayCd = JkTools.getCalendar();
            todayCd.add(Calendar.HOUR_OF_DAY, client.addHours);
            signModel.clearLastMonSave(todayCd);

            new SignListRspd(client, signModel.checkedDays, signModel.boxGetDays, signModel.weekRewardSet);
        }

        if(isSendMsg(isLogin,ModuleUIEnum.REDNESS_PALACE)){
            if(RednessModel.campUserPVo != null){
                HashMap<Long,Integer> map = LoopSendMailModel.rankUsers.get(RednessModel.lastMailID);
                int myLastIndex = -1;
                if(map != null && map.containsKey(guid))myLastIndex = map.get(guid);
                new RP_LastRednessWinnerRspd(client,RednessModel.campUserPVo,RednessModel.lastRednessRank,myLastIndex);
            }
        }

        if (isSendMsg(isLogin, ModuleUIEnum.MY_FRIEND)) {
            if(!friendModel.loadSql){
                AllSql.friendLogSql.loadOneFriendLog(this);
                AllSql.friendGiftSql.loadOneFriendGift(this);
                friendModel.loadSql = true;
            }
            new PickMoneyRspd(client,-1,friendModel.canPickCount);
            new ReceiveTiliRspd(client,-1,friendModel.canReceiveCount);
            new MyAttentionRspd(client,FriendModel.getMyAttention(guid));
        }
        if(isSendMsg(isLogin,ModuleUIEnum.UNION_VIEW)){
            if(cacheUserVo.gang.gangVo != null){
                GangVo gangVo = cacheUserVo.gang.gangVo;
                gangSkillModel.checkUp();
                if(!gangVo.users.containsKey(guid))return;
                GangUserVo vo = cacheUserVo.gang.gangVo.users.get(guid);
                if(vo == null)return;
                new GetBoxIDRspd(client,vo.getBoxID);
                new GangUserDataRspd(client,JkTools.intArrayAsList(vo.userdata));
                if(vo.isGetGift == 0){
                    GameSetModel.checkTime(1);
                    new GiftListRspd(client,gangVo.giftMap.values());
                }
                new GangDragonRspd(client,gangVo.mapModel.gangDragon.value,gangVo.mapModel.gangDragon.key);
            }
        }

        if(isSendMsg(isLogin,ModuleUIEnum.SKIN)){
            if(cacheUserVo.skinModel.skinMap.size() > 0){
                new SkinListRspd(client,cacheUserVo.skinModel.skinMap.values());
            }
        }

        if(isSendMsg(isLogin,ModuleUIEnum.CLOSED_TEST_GIFT)){
            if(isGetCloseTest == 1){
                new ClosedTestTimeRspd(client,(byte)1,hadGetTime);
            }else{
                if(hadGetTime == -1 && ClosedTestModel.IDENTIFY_SUCCESS.equals(client.passportVo.identifyCode)){
                    hadGetTime = 0;
                    AllSql.userSql.update(this,AllSql.userSql.FIELD_HAD_GET_TIME,0);
                }
                new ClosedTestTimeRspd(client,(byte)0,hadGetTime);
            }
        }

        if(isSendMsg(isLogin,ModuleUIEnum.ELITE)){
            new ElitePassedMapRspd(client,eliteModel.elitePassedMap);
        }

        if(isSendMsg(isLogin,ModuleUIEnum.MINERAL_RES)){
            MineModel mineModel = cacheUserVo.mineModel;
            if(mineModel.endTime != 0){
                ArrayList<TeamUserPVo> users = new ArrayList<>();
                for (Map.Entry<Byte, MineRecruitVo> item : mineModel.teamMap.entrySet()) {
                    TeamUserPVo teamUserPVo = new TeamUserPVo();
                    teamUserPVo.index = item.getKey();
                    teamUserPVo.userID = item.getValue().guid;
                    teamUserPVo.level = item.getValue().cacheUserVo.level;
                    teamUserPVo.portrait = item.getValue().cacheUserVo.portrait;
                    users.add(teamUserPVo);
                }
                mineModel.sendLootMsg = false;
                new MR_ExploitRspd(client,mineModel.mineType,mineModel.total,mineModel.stoneList,mineModel.lostMineCount,mineModel.lostStoneCount,mineModel.startTime,mineModel.endTime,users);
            }
        }

        if(isSendMsg(isLogin,ModuleUIEnum.WING)){
            if(cacheUserVo.wingModel.coreMap.size() == 0){
                WingCorePVo wingCorePVo = new WingCorePVo();
                wingCorePVo.id = 1;
                wingCorePVo.level = 1;
                cacheUserVo.wingModel.coreMap.put((byte)1,wingCorePVo);
                cacheUserVo.wingModel.saveSqlData();
            }
            new WingCoreListRspd(client,cacheUserVo.wingModel.coreMap.values());
        }

        if(isSendMsg(isLogin,ModuleUIEnum.HERO_TAG)){
            heroTagModel.zdlTagMap.clear();
            heroTagModel.activeTagMap.clear();
            cacheUserVo.addHeroTagList.clear();
            AllSql.heroTagSql.loadOneHeroTag(this);
            ArrayList<HeroTagPVo> zdlTagList = new ArrayList<>();
            ArrayList<HeroTagPVo> activeTagList = new ArrayList<>();
            for(HeroTagVo heroTagVo : heroTagModel.zdlTagMap.values()){
                HeroTagPVo heroTagPVo = new HeroTagPVo();
                heroTagPVo.id = heroTagVo.tagID;
                heroTagPVo.status = heroTagVo.status;
                heroTagPVo.endTime = heroTagVo.deadTime;
                zdlTagList.add(heroTagPVo);
            }
            for(HeroTagVo heroTagVo : heroTagModel.activeTagMap.values()){
                HeroTagPVo heroTagPVo = new HeroTagPVo();
                heroTagPVo.id = heroTagVo.tagID;
                heroTagPVo.status = heroTagVo.status;
                heroTagPVo.endTime = heroTagVo.deadTime;
                activeTagList.add(heroTagPVo);
            }
            new HeroTagInfoRspd(client,cacheUserVo.heroTagID,zdlTagList,activeTagList);
        }

        if(isSendMsg(isLogin,ModuleUIEnum.EMPEROR)){
            new EmperorListRspd(client,emperorModel.emperorMap.values());
            new EmperorAchieveRspd(client,emperorModel.achieveMap.values());
            new EmperorAttributeListRspd(client,emperorModel.attributeMap.values());
        }

        if(isSendMsg(isLogin,ModuleUIEnum.REALM)){
            ArrayList<RealmPVo> list = new ArrayList<>();
            for(RealmVo realmVo : realmModel.realmMap.values()){
                list.add(realmVo.toRealmPVo());
            }
            new RealmRspd(client,list);
        }

        if (isLevelUp || isUpdateZDL == true) {
            updateZDL();
        }
    }

    private boolean isSendMsg(boolean isLogin, byte function) {
        if (isLogin) {
            if (functionSet.contains(function)) return true;
        }
        if (isOpenNow(function)) return true;
        return false;
    }

    private boolean isOpenNow(byte function) {
        if (!functionSet.contains(function) && isOpen(function)) {
            functionSet.add(function);
            return true;
        }
        return false;
    }

    public boolean isOpen(byte function) {
        FunctionOpenBaseVo functionVo = Model.FunctionOpenBaseMap.get((int) function);
        if(functionVo==null)return false;
        if (getUserData(UserDataEnum.LEVEL) >= functionVo.needLevel) {
            if(functionVo.needPass == 0 || mapQualityMap.containsKey(functionVo.needPass)){
                return true;
            }
        }
        return false;
    }

    private void levelAiring() {
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(6);
        if (airingVo == null) return;
        if (airingVo.isUse != 1) return;
        int level = getUserData(UserDataEnum.LEVEL);
        if (level % airingVo.divisor != 0) return;
        if (JkTools.compare(level, airingVo.conditionParams[1], airingVo.conditionParams[0]) == false) return;
        if (airingVo.conditionParams.length >= 4 && JkTools.compare(level, airingVo.conditionParams[3], airingVo.conditionParams[2]) == false)
            return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
//        pVo.msg = "恭喜 "+cacheUserVo.passportVo.name+" 等级提升至 "+level+" 级。";
        pVo.msg = airingVo.msg.replace("{1}", cacheUserVo.name).replace("{2}", String.valueOf(level));
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }

    private void rankAiring(int type) {
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(9);
        if (airingVo == null) return;
        if (airingVo.isUse != 1) return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        switch (type) {
            case RankEnum.ZDL:
                int index = cacheUserVo.rankVo.orderIndex + 1;
                if (index % airingVo.divisor != 0) return;
                if (JkTools.compare(index, airingVo.conditionParams[1], airingVo.conditionParams[0]) == false) return;
                if (airingVo.conditionParams.length >= 4 && JkTools.compare(index, airingVo.conditionParams[3], airingVo.conditionParams[2]) == false)
                    return;
                pVo.type = 1;
//                pVo.msg = "恭喜 "+cacheUserVo.passportVo.name+" 战力提升至 "+ zdl + " 成为战力排行榜第 " +index+" 。";
                pVo.msg = airingVo.msg.replace("{1}", cacheUserVo.name).replace("{2}", String.valueOf(zdl)).replace("{3}", String.valueOf(index));
                pVo.time = 1;
                break;
        }
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
