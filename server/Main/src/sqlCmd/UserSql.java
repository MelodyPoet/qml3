package sqlCmd;

import arena.ArenaModel;
import arena.RankUserArenaVo;
import base.*;
import comm.*;
import gluffy.utils.JkTools;
import mine.MineModel;
import mine.RankMineVo;
import mission.MissionModel;
import prop.PropModel;
import protocol.*;
import rank.RankModel;
import redness.RankRednessVo;
import table.*;
import talk.TalkModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserSql extends BaseSql {
    public final String FIELD_PASSPORT = "passport";
    public final String FIELD_BASEID = "baseID";
    public final String FIELD_MONEY = "money";
    public final String FIELD_EXP = "exp";
    public final String FIELD_LEVEL = "level";
    public final String FIELD_ZDL = "zdl";
    public final String FIELD_SKILL = "skills";
    public final String FIELD_MAPQUALITY = "mapQuality";
    public final String FIELD_TILIZHI = "tilizhi";
    public final String FIELD_RESETDAY = "lastResetDay";
    public final String FIELD_DIAMOND = "diamond";
    public final String FIELD_FIGHT = "fightData";
    public final String FIELD_SKILLEXP = "skillExp";
    public final String FIELD_HPDRUG = "hpDrug";
    public final String FIELD_ARENA = "arena";
    public final String FIELD_MAIN_MISSION = "main_mission";
    public final String FIELD_DAILY_MISSION = "daily_mission";
    public final String FIELD_ACHIEVE_MISSION = "achieve_mission";
    public final String FIELD_CREATED_TIME = "createdTime";
    public final String FIELD_DRAGON = "dragon";
    public final String FIELD_BAG_CELL_HAVE_COUNT = "bag_cell_have_count";
    public final String FIELD_BAG_CELL_CD_COUNT = "bag_cell_cd_count";
    public final String FIELD_NEXTTIME_BAG_CELL = "nexttime_bag_cell";
    public final String FIELD_BAG_REMOVE_CD_TIME = "bag_remove_cd_time";
    public final String FIELD_LAST_LOGIN_TIME = "lastLoginTime";
    public final String FIELD_WORM_NEST_CURRENT_FLOOR = "wormNestCurrentFloor";
    public final String FIELD_WORM_NEST_MAX_FLOOR = "wormNestMaxFloor";
    public final String FIELD_WORM_NEST_MAX_FLOOR_TIME = "wormNestMaxFloorTime";
    public final String FIELD_WORM_NEST_NEXT_END_TIME = "wormNestNextEndTime";
    public final String FIELD_WORM_NEST_RESET_TIME = "wormNestResetTime";
    public final String FIELD_IS_GET_GANG_AWARD = "isGetGangAward";
    public final String FIELD_TALENT_CD = "talentCD";
    public final String FIELD_ARENA_FIGHT_COUNT = "arenaFightCount";
    public final String FIELD_EXCHANGE_TILI_COUNT = "exchangeTiliCount";
    public final String FIELD_CREDIT = "credit";
    public final String FIELD_DRAGON_STONE_BUY_COUNT = "dragonStoneBuyCount";
    public final String FIELD_PROP_SHOP_FREE_FLUSH_TIME = "propShopFreeFlushTime";
    public final String FIELD_PROP_SHOP_FLUSH_CD = "propShopFlushCD";
    public final String FIELD_PROP_SHOP_GROUP = "propShopGroup";
    public final String FIELD_NEW_MSG_ITEM = "newMsgItem";
    public final String FIELD_ILLUSTRATED = "illustrated";
    public final String FIELD_ARENA_RANK = "arenaRank";
    public final String FIELD_ARENA_AWARD_FLAG = "arenaAwardFlag";
    public final String FIELD_SKIN_LIST = "skinList";
    public final String FIELD_SKIN = "skin";
    public final String FIELD_HAD_GET_TIME = "hadGetTime";
    public final String FIELD_IS_GET_CLOSE_TEST = "isGetCloseTest";
    public final String FIELD_GUIDE = "guide";
    public final String LJ_REFINE_COUNT = "lj_refine_count";
    public final String LJ_SNATCH_COUNT = "lj_snatch_count";
    public final String LJ_ARENA_COUNT = "lj_arena_count";
    public final String LJ_STARS_COUNT = "lj_stars_count";
    public final String LJ_COST_MONEY_COUNT = "lj_cost_money_count";
    public final String LJ_COST_DIAMOND_COUNT = "lj_cost_diamond_count";
    public final String LJ_RECHARGE_DIAMOND_COUNT = "lj_recharge_diamond_count";
    public final String LJ_SIGN_COUNT = "lj_sign_count";
    public final String LJ_DRAGON_COUNT = "lj_dragon_count";
    public final String LJ_DISSOLVE_EQUIP_COUNT = "lj_dissolve_equip_count";
    public final String LJ_REDPACKET_VALUE = "lj_redpacket_value";
    public final String LJ_MONEY_BUY_DRAGON_STONE_COUNT = "lj_money_buy_dragon_stone_count";
    public final String LJ_DIAMOND_BUY_DRAGON_STONE_COUNT = "lj_diamond_buy_dragon_stone_count";
    public final String LJ_MONEY_BUY_DRAGON_COUNT = "lj_money_buy_dragon_count";
    public final String LJ_DIAMOND_BUY_DRAGON_COUNT = "lj_diamond_buy_dragon_count";
    public final String LJ_BUY_TILI_COUNT = "lj_buy_tili_count";
    public final String LJ_UP_DRAGON_STONE_COUNT = "lj_up_dragon_stone_count";
    public final String LJ_TOUCH_COUNT = "lj_touch_count";
    public final String LJ_EQUIP_INTENSIFY_COUNT = "lj_equip_intensify_count";
    public final String LJ_SHOP_FLUSH_COUNT = "lj_shop_flush_count";
    public final String LJ_TALK_COUNT = "lj_talk_count";
    public final String LJ_LIKE_TALK_COUNT = "lj_like_talk_count";
    public final String LJ_NORMAL_PASS_COUNT = "lj_normal_pass_count";
    public final String LJ_YOU_MIN_PASS_COUNT = "lj_you_min_pass_count";
    public final String LJ_YUAN_SU_PASS_COUNT = "lj_yuan_su_pass_count";
    public final String LJ_DRAGON_PASS_COUNT = "lj_dragon_pass_count";
    public final String MAX_DRAGON_LEVEL = "max_dragon_level";
    public final String MAX_EQUIP_INTENSIFY_LEVEL = "max_equip_intensify_level";
    public final String MAX_JEWELRY_INTENSIFY_LEVEL = "max_jewelry_intensify_level";
    public final String MAX_TALENT_LEVEL = "max_talent_level";
    public final String FIELD_OWN_DRAGON_STONE_LEVEL = "own_dragon_stone_level";
    public final String FIELD_MAPENTERED = "mapEntered";
    public final String FIELD_LAST_RUN_DAY = "lastRunDay";
    public final String FIELD_FREE_LOTTERY_DRAGON = "freeLotteryDragon";
    public final String LJ_DIAMOND_BUY_ONE_DRAGON_COUNT = "lj_diamond_buy_one_dragon_count";
    public final String FIELD_GANG_SKILL = "gangSkill";
    public final String FIELD_DRAGON_STONE_EQUIP = "dragonStoneEquip";
    public final String FIELD_NEXT_TIME_TILI = "nextTimeTili";
    public final String FIELD_BUY_VIP_GIFT = "buyVipGift";
    public final String FIELD_EXCHANGE_ARENA_COUNT = "exchangeArenaCount";
    public final String FIELD_NEXT_TIME_JOIN_GANG = "nextTimeJoinGang";
    public final String FIELD_GANG_STATUS = "gangStatus";
    public final String FIELD_DRAGON_EGG = "dragonEgg";
    public final String FIELD_CAN_TOUCH_COUNT = "canTouchCount";
    public final String FIELD_YEAR_CARD = "yearCard";
    public final String FIELD_MONTH_CARD = "monthCard";
    public final String FIELD_GET_TILI = "getTili";
    public final String FIELD_YEAR_CARD_END_TIME = "yearCardEndTime";
    public final String FIELD_MONTH_CARD_END_TIME = "monthCardEndTime";
    public final String FIELD_SIGN_INFO = "signInfo";
    public final String FIELD_MONEY_TREE_COUNT = "moneyTreeCount";
    public final String FIELD_MONEY_TREE_NEXT_TIME = "moneyTreeNextTime";
    public final String FIELD_NORMAL_RESET_COUNT = "normalResetCount";
    public final String FIELD_DAILY_LIVENESS = "dailyLiveness";
    public final String FIELD_LIVENESS = "liveness";
    public final String FIELD_EQUIP_QUALITY = "equipQuality";
    public final String FIELD_CARNET_COUNT = "carnetCount";
    public final String FIELD_STONE_COUNT = "stoneCount";
    public final String FIELD_LJ_TALENT_UP_COUNT = "lj_talent_up_count";
    public final String FIELD_LJ_DRAGON_EGG_COUNT = "lj_dragon_egg_count";
    public final String FIELD_LJ_HATCH_DRAGON_STONE = "lj_hatch_dragon_stone";
    public final String FIELD_NAME = "uname";
    public final String FIELD_LIMIT_TIME_ACTIVITY = "limitTimeActivity";
    public final String FIELD_LJ_DRAGON_STONE_COUNT = "lj_dragon_stone_count";
    public final String FIELD_LJ_LIVENESS = "lj_liveness";
    public final String FIELD_LJ_DRAGON_UP_COUNT = "lj_dragon_up_count";
    public final String FIELD_RED_NESS_MONEY = "rednessMoney";
    public final String FIELD_WEEK_REDNESS_MONEY = "weekRednessMoney";
    public final String FIELD_CHANNEL_STR = "channelStr";
    public final String FIELD_FRIEND = "friend";
    public final String FIELD_YUNWA_ID = "yunwaID";
    public final String FIELD_LAST_WEEK_DAY = "lastWeekDay";
    public final String FIELD_LAST_UPDATE_TIME = "lastUpdateTime";
    public final String FIELD_DRAGON_CACHE = "dragonCache";
    public final String FIELD_ELITE_BUY_COUNT = "eliteBuyBount";
    public final String FIELD_ELITE = "elite";
    public final String FIELD_MAX_WORLD_BOSS_HURT = "max_world_boss_hurt";
    public final String FIELD_LJ_WORLD_BOSS_HURT = "lj_world_boss_hurt";
    public final String FIELD_ATTRIBUTE = "attribute";
    public final String FIELD_EGG_QUALITY = "eggQuality";
    public final String FIELD_USED_GIFT = "usedGift";
    public final String FIELD_IS_ROBOT = "isRobot";
    public final String FIELD_PORTRAIT = "portrait";
    public final String FIELD_OPEN_EQUIP_BAG_COUNT = "openEquipBagCount";
    public final String FIELD_EQUIP_BAG_COUNT = "equipBagCount";
    public final String FIELD_MR_MINE_COUNT = "mr_mine_count";
    public final String FIELD_MR_EXPLOIT_COUNT = "mr_exploit_count";
    public final String FIELD_MR_SEARCH_COUNT = "mr_search_count";
    public final String FIELD_MINE = "mine";
    public final String FIELD_MR_FREE_RECRUIT_COUNT = "mr_free_recruit_count";
    public final String FIELD_MR_LOOT_COUNT = "mr_loot_count";
    public final String FIELD_WING = "wing";
    public final String FIELD_LOTTERY_PROP_FREE_COUNT = "lottery_prop_free_count";
    public final String FIELD_LOTTERY_STONE_COUNT = "lottery_stone_count";
    public final String FIELD_LOTTERY_DRAGON_COUNT = "lottery_dragon_count";
    public final String FIELD_LOTTERY_STONE_NEXTTIME = "lottery_stone_nexttime";
    public final String FIELD_LOTTERY_DRAGON_NEXTTIME = "lottery_dragon_nexttime";
    public final String FIELD_HERO_TAG_ID = "hero_tag_id";
    public final String FIELD_SNATCH_FIGHT_COUNT = "snatch_fight_count";
    public final String FIELD_NEXTTIME_SNATCH = "nexttime_snatch";
    public final String FIELD_TALENT_EXP = "talent_exp";
    public final String FIELD_LJ_LOTTERY_COUNT = "lj_lottery_count";
    public final String FIELD_EMPEROR = "emperor";
    public final String FIELD_REALM = "realm";
    public UserSql() {
        super("user");
        initCacheUser();
    }

    public void isInitDataBase(){
        initDataBase();
        initCacheUser();
    }

    public void initDataBase(){
        Connection conn = SqlPool.getConn();
        String cmd;
        try {
            int i =0;
            ArrayList<RobotBaseVo> robotList = Model.RobotBaseMap.get((int)RobotType.ARENA);
            for(RobotBaseVo robot : robotList){
                long guid=AllSql.userSql.getNewGuid();
                cmd = "insert into user("+FIELD_GUID+","+FIELD_PASSPORT+","+FIELD_BASEID+","+FIELD_NAME+","+FIELD_CREATED_TIME+","+FIELD_LEVEL+","+FIELD_ZDL+","+FIELD_IS_ROBOT+","+FIELD_ARENA_RANK+
                        ","+FIELD_WORM_NEST_MAX_FLOOR+","+FIELD_WORM_NEST_MAX_FLOOR_TIME+","+FIELD_PORTRAIT+
                        ") " +
                        "values("+guid+ ","+guid+","+ robot.baseID+ ","+ "\'"+ robot.name +"\',"+ JkTools.getGameServerTime(null)+ ","+ robot.level+ ","+ robot.zdl+ ","+ 1 + ","+ i +
                        ","+ (Model.RobotBaseMap.size()-i) +","+ i++ +","+ robot.portrait+
                        ")";
                Statement state = conn.createStatement();
                state.executeUpdate(cmd);
            }
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
        } finally {
            SqlPool.release(conn);
        }
    }

    public boolean initCacheUser() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            conn.setAutoCommit(true);
            cmd = "select guid,passport,baseID,level,zdl,skills,lastLoginTime,wormNestMaxFloor,wormNestMaxFloorTime,skin,gangStatus,dragonEgg,uname,yunwaID,skinList,dragonCache,attribute,isRobot,portrait,mine,wing,hero_tag_id  from  user";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                CacheUserVo cuVo = new CacheUserVo();

                cuVo.guid = dataSet.getLong(1);
                cuVo.passportVo= CachePassportVo.guidMap.get(dataSet.getLong(2));
                cuVo.baseID = dataSet.getByte(3);
                cuVo.level = dataSet.getByte(4);
                cuVo.zdl = dataSet.getInt(5);
                cuVo.skillModel.loadData(dataSet.getString(6));
                cuVo.lastLoginTime = dataSet.getInt(7);
                cuVo.wormNestMaxFloor = dataSet.getInt(8);
                cuVo.wormNestMaxFloorTime = dataSet.getInt(9);
//                cuVo.mainDragonID = loadMainDragonID(dataSet.getString(10));
                cuVo.skin = dataSet.getShort(10);
                cuVo.gangStatus = dataSet.getByte(11);
                cuVo.dragonEggModel.loadData(dataSet.getString(12));
                cuVo.name = dataSet.getString(13);
                cuVo.yunwaID = dataSet.getInt(14);
                cuVo.skinModel.loadData(dataSet.getString(15));
                cuVo.dragonCacheModel.loadData(dataSet.getString(16));
                cuVo.attributeModel.loadData(dataSet.getString(17));
                int isRobot = dataSet.getByte(18);
                cuVo.portrait = dataSet.getByte(19);
                cuVo.mineModel.loadData(dataSet.getString(20));
                cuVo.wingModel.loadData(dataSet.getString(21));
                cuVo.heroTagID = dataSet.getShort(22);
                if(isRobot == 1){
                    ArrayList<RobotBaseVo> robotList = Model.RobotBaseMap.get((int)RobotType.ARENA);
                    for(RobotBaseVo robot : robotList){
                        if(robot.baseID != cuVo.baseID || robot.level != cuVo.level)continue;
                        for(int propId: robot.equip){
                            if(!PropModel.isEquip(propId))continue;
                            PropBaseVo propBaseVo = Model.PropBaseMap.get(propId);
                            PropPVo pVo = new PropPVo();
                            pVo.baseID = propId;
                            pVo.count = 1;

                            switch (propBaseVo.type){
                                case PropTypeEnum.MAIN_WEAPON: cuVo.equipItems[PropTypeEnum.MAIN_WEAPON] = pVo;break;
                                case PropTypeEnum.BODY: cuVo.equipItems[PropTypeEnum.BODY] = pVo;break;
                                case PropTypeEnum.HEAD: cuVo.equipItems[PropTypeEnum.HEAD] = pVo;break;
                                case PropTypeEnum.HAND: cuVo.equipItems[PropTypeEnum.HAND] = pVo;break;
                                case PropTypeEnum.FEET: cuVo.equipItems[PropTypeEnum.FEET] = pVo;break;
                                case PropTypeEnum.PANTS: cuVo.equipItems[PropTypeEnum.PANTS] = pVo;break;
                                case PropTypeEnum.RING: cuVo.equipItems[PropTypeEnum.RING] = pVo;break;
                                case PropTypeEnum.NECK: cuVo.equipItems[PropTypeEnum.NECK] = pVo;break;
                            }
                        }
                    }
                }

                 //cuVo.passportVo.devID= cuVo.name;
                //CachePassportVo.devMap.put(  cuVo.passportVo.devID,cuVo.passportVo);
                CacheUserVo.allMap.put(cuVo.guid,cuVo);
                CacheUserVo.usedName.add(cuVo.name);
                if(cuVo.passportVo==null){
                    System.out.println("errorPassportVo,guid"+dataSet.getLong(2));
                }
                if( cuVo.passportVo!=null)
                cuVo.passportVo.userMap.put(cuVo.baseID,cuVo);
//                if(cuVo.rankArenaVo==null){
//                    cuVo.rankArenaVo=new RankUserArenaVo(cuVo);
//                    RankModel.rankArenaList.addEnd(cuVo.rankArenaVo);
//                }

                String cmd2 = "select baseID from  prop  where tableID=" + PropCellEnum.EQUIP + " and userID=" + cuVo.guid;
                ResultSet set = conn.createStatement().executeQuery(cmd2);
                while (set.next()) {
                    PropPVo pVo = new PropPVo();

                    pVo.baseID = set.getInt(1);

                    cuVo.equipItems[Model.PropBaseMap.get(pVo.baseID).type]=pVo;
                }
                set.close();

//                cuVo.equipDragonStone=new ArrayList<>();
//                String cmd3 = "select guid,baseID,count from  prop  where tableID=" + PropCellEnum.DRAGON_STONE + " and userID=" + cuVo.guid;
//                  set  = conn.createStatement().executeQuery(cmd3);
//                while (set .next()) {
//                    PropPVo pVo = new PropPVo();
//                    pVo.tempID = set.getInt(1);
//                    pVo.baseID = set .getInt(2);
//                    pVo.count = set.getInt(3);
//
//                    cuVo.equipDragonStone.add(pVo);
//                }
//                set.close();
            }
            dataSet.close();
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public void initRank(){
        initArenaRank();
        initRednessRank();
        initMineRank();
    }

    public boolean initArenaRank() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            conn.setAutoCommit(true);
            cmd = "select guid,arenaRank,arena  from  user where arenaRank >= 0 order by arenaRank";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                CacheUserVo cuVo = CacheUserVo.allMap.get(dataSet.getLong(1));
                RankUserArenaVo rankArenaVo=new RankUserArenaVo(cuVo);
                rankArenaVo.orderIndex = dataSet.getInt(2);
                rankArenaVo.arenaRecordList = ArenaModel.loadArenaRecord(dataSet.getString(3));
                RankModel.rankArenaList.addEnd(rankArenaVo);
            }
            dataSet.close();
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean initRednessRank() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            conn.setAutoCommit(true);
            int time = JkTools.getMondayBeginTime(null);
            cmd = "select guid,weekRednessMoney  from  user where weekRednessMoney > 0 and lastUpdateTime > "+time+" order by weekRednessMoney desc";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                CacheUserVo cuVo = CacheUserVo.allMap.get(dataSet.getLong(1));
                RankRednessVo rankRednessVo=new RankRednessVo(cuVo);
                rankRednessVo.weekRednessMoney = dataSet.getInt(2);
                RankModel.rankRednessList.addEnd(rankRednessVo);
            }
            dataSet.close();
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean initMineRank() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            conn.setAutoCommit(true);
            int time = JkTools.getRundayTime(null);
            cmd = "select guid,mr_mine_count  from  user where mr_mine_count > 0 and lastUpdateTime > "+time+" order by mr_mine_count desc";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                CacheUserVo cuVo = CacheUserVo.allMap.get(dataSet.getLong(1));
                RankMineVo rankMineVo=new RankMineVo(cuVo);
                rankMineVo.mineCount = dataSet.getInt(2);
                if(RankModel.rankMineList.size() < MineModel.RANK_COUNT){
                    RankModel.rankMineList.addEnd(rankMineVo);
                }
            }
            dataSet.close();
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean loadone(Client client, CacheUserVo ccUser) {
        if (client.userMap.containsKey(ccUser.baseID)) {
            client.currentUser = client.userMap.get(ccUser.baseID);
            return true;
        }
        ResultSet set = readSet(FIELD_GUID + "=" + ccUser.guid + " limit 1");
        if (set == null) return false;
        try {
            if (set.next() == false) return false;

            User us = new User(client);
            int index = 7;
            us.guid = ccUser.guid;
            us.cacheUserVo = ccUser;

            us.baseID = ccUser.baseID;
            us.userdata[UserDataEnum.MONEY] = set.getInt(index++);
            us.userdata[UserDataEnum.EXP] = set.getInt(index++);
            us.userdata[UserDataEnum.LEVEL] = ccUser.level;
            us.zdl = ccUser.zdl;
            us.initWithCache();
            int[] tempAry;

            tempAry = JkTools.readArray(set.getString(index++), ",");


            if (tempAry != null) {
                for (int i = 0; i < tempAry.length - 1; ) {
                    MapQualityPVo mpvo = new MapQualityPVo();
                    mpvo.stars = new ArrayList<>();
                    i += MapQualityPVoJoin.instance.fromSplitStr(mpvo, tempAry, i);
                    us.mapQualityMap.put(mpvo.ID, mpvo);
                }
            }

            us.userdata[UserDataEnum.TILIZHI] = set.getInt(index++);
            us.userdata[UserDataEnum.LAST_DAY] = set.getInt(index++);
            us.userdata[UserDataEnum.DIAMOND] = set.getInt(index++);
            index++;//  tempAry = JkTools.readArray(set.getString(index++), ",");
           // if (tempAry != null) {
            //  loadFightData(us, tempAry);
           // }
            set.getInt(index++);//UserDataEnum.SKILL_EXP 不再需要
            us.userdata[UserDataEnum.HP_COUNT] = set.getByte(index++);
            us.arenaModel.loadData(set.getString(index++));
            us.missionModel.loadData(set.getString(index++));
            us.activationModel.loadData(set.getString(index++));
            us.achieveModel.loadData(set.getString(index++));
            us.userdata[UserDataEnum.RoleCreatTime]= set.getInt(index++);
            us.dragonModel.loadData(set.getString(index++));
            us.userdata[UserDataEnum.BAG_CELL_HAVE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.BAG_CELL_CD_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.NEXTTIME_BAG_CELL] = set.getInt(index++);
            us.userdata[UserDataEnum.BAG_REMOVE_CD_TIME] = set.getInt(index++);
            us.userdata[UserDataEnum.LAST_LOGIN_TIME] = set.getInt(index++);
            us.userdata[UserDataEnum.WormNestCurrentFloor] = set.getInt(index++);
            us.userdata[UserDataEnum.WormNestMaxFloor] = set.getInt(index++);
            us.userdata[UserDataEnum.WormNestMaxFloorTime] = set.getInt(index++);
            us.userdata[UserDataEnum.WormNestNextEndTime] = set.getInt(index++);
            us.userdata[UserDataEnum.WormNestResetTime] = set.getByte(index++);
            us.userdata[UserDataEnum.IsGetGangAward] = set.getByte(index++);
            us.userdata[UserDataEnum.Talent_CD] = set.getInt(index++);
            us.userdata[UserDataEnum.ARENA_FIGHT_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.EXCHANGE_TILI_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.CREDIT] = set.getInt(index++);
            us.userdata[UserDataEnum.DRAGON_STONE_BUY_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.PropShopFreeFlushTime] = set.getByte(index++);
            us.userdata[UserDataEnum.PropShopFlushCD] = set.getInt(index++);
            us.propModel.loadData(set.getString(index++));
            TalkModel.loadUserNewMsg(us,set.getString(index++));
            us.illustratedModel.loadData(set.getString(index++));

            //arenaRank和arenaAwardFlag在initArenaRank方法中加载
            set.getInt(index++);
            set.getInt(index++);
            set.getString(index++);//皮肤列表
            set.getInt(index++);//默认皮肤在加载缓存时加载
            us.hadGetTime = set.getByte(index++);
            us.isGetCloseTest = set.getByte(index++);
            us.guide = set.getString(index++);
            if(us.guide == null) us.guide = "";
            us.userdata[UserDataEnum.LJ_REFINE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_SNATCH_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_ARENA_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_STARS_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_COST_MONEY_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_COST_DIAMOND_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_RECHARGE_DIAMOND_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_SIGN_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DRAGON_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DISSOLVE_EQUIP_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_REDPACKET_VALUE] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_MONEY_BUY_DRAGON_STONE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_MONEY_BUY_DRAGON_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DIAMOND_BUY_DRAGON_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_BUY_TILI_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_UP_DRAGON_STONE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_TOUCH_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_EQUIP_INTENSIFY_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_SHOP_FLUSH_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_TALK_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_LIKE_TALK_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_NORMAL_PASS_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_YOU_MIN_PASS_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_YUAN_SU_PASS_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DRAGON_PASS_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.MAX_DRAGON_LEVEL] = set.getInt(index++);
            us.userdata[UserDataEnum.MAX_EQUIP_INTENSIFY_LEVEL] = set.getInt(index++);
            us.userdata[UserDataEnum.MAX_JEWELRY_INTENSIFY_LEVEL] = set.getInt(index++);
            us.userdata[UserDataEnum.MAX_TALENT_LEVEL] = set.getInt(index++);
            us.dragonStoneOwnLevel = set.getString(index++);
            if(us.dragonStoneOwnLevel == null)us.dragonStoneOwnLevel = "";

            int[] mapEnterAry;
            mapEnterAry = JkTools.readArray(set.getString(index++), ",");
            if (mapEnterAry != null) {
                for (int i = 0; i < mapEnterAry.length - 1; ) {
                    MapEnteredPVo sspvo = new MapEnteredPVo();
                    i += MapEnteredPVoJoin.instance.fromSplitStr(sspvo, mapEnterAry, i);
                    us.mapEnteredMap.put(sspvo.key, sspvo);
                }
            }
            us.userdata[UserDataEnum.LAST_RUN_DAY] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DIAMOND_BUY_ONE_DRAGON_COUNT] = set.getInt(index++);
            us.gangSkillModel.loadData(set.getString(index++));
            us.dragonModel.loadDragonStoneEquip(set.getString(index++));
            us.userdata[UserDataEnum.NEXTTIME_Tili] = set.getInt(index++);
            us.buyVipGift = set.getString(index++);
            if(us.buyVipGift == null)us.buyVipGift = "";
            us.userdata[UserDataEnum.EXCHANGE_ARENA_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.NEXT_TIME_JOIN_GANG] = set.getInt(index++);
            index++;
            index++;
            us.userdata[UserDataEnum.CAN_TOUCH_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.YEAR_CARD] = set.getByte(index++);
            us.userdata[UserDataEnum.MONTH_CARD] = set.getByte(index++);
            us.getTili = set.getString(index++);
            if(us.getTili == null)us.getTili = "";
            us.userdata[UserDataEnum.YEAR_CARD_END_TIME] = set.getInt(index++);
            us.userdata[UserDataEnum.MONTH_CARD_END_TIME] = set.getInt(index++);
            us.signModel.loadData(set.getString(index++));
            us.userdata[UserDataEnum.MONEY_TREE_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.MONEY_TREE_NEXT_TIME] = set.getInt(index++);
            us.userdata[UserDataEnum.NORMAL_RESET_COUNT] = set.getByte(index++);
            us.userdata[UserDataEnum.DAILY_LIVENESS] = set.getInt(index++);
            us.userdata[UserDataEnum.LIVENESS] = set.getInt(index++);
            int[] equipQuality = JkTools.readArray(set.getString(index++),",");
            if(equipQuality != null){
                for(int i=0;i<equipQuality.length-1;){
                    ByteIntPVo byteIntPVo = new ByteIntPVo();
                    i += EquipQualityPVoJoin.instance.fromSplitStr(byteIntPVo,equipQuality,i);
                    us.equipQualityMap.put(byteIntPVo.key,byteIntPVo);
                }
            }
            int[] carnetCount = JkTools.readArray(set.getString(index++),",");
            if(carnetCount != null){
                for(int i=0;i<carnetCount.length-1;){
                    IntIntPVo intIntPVo = new IntIntPVo();
                    i += CarnetCountPVoJoin.instance.fromSplitStr(intIntPVo,carnetCount,i);
                    us.carnetCountMap.put(intIntPVo.key,intIntPVo);
                }
            }
            int[] stoneCount = JkTools.readArray(set.getString(index++),",");
            if(stoneCount != null){
                for(int i=0;i<stoneCount.length-1;){
                    ByteIntPVo byteIntPVo = new ByteIntPVo();
                    i += StoneCountPVoJoin.instance.fromSplitStr(byteIntPVo,stoneCount,i);
                    us.stoneCountMap.put(byteIntPVo.key,byteIntPVo);
                }
            }
            us.userdata[UserDataEnum.LJ_TALENT_UP_COUNT] =set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DRAGON_EGG_COUNT] =set.getInt(index++);
            us.userdata[UserDataEnum.LJ_HATCH_DRAGON_STONE] =set.getInt(index++);
            index++;//name
            MissionModel.loadLimitTime(us,set.getString(index++));
            us.userdata[UserDataEnum.LJ_DRAGON_STONE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_LIVENESS] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_DRAGON_UP_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.RED_NESS_MONEY] = set.getInt(index++);
            index++;//周魂印数
            index++;//聊天通配符
            us.friendModel.loadData(set.getString(index++));
            index++;//云娃ID
            us.lastWeekDay = set.getInt(index++);
            us.userUpdateModel.lastUpdateTime = set.getInt(index++);
            index++;//龙魂缓存
            us.userdata[UserDataEnum.ELITE_BUY_COUNT] = set.getByte(index++);
            us.eliteModel.loadData(set.getString(index++));
            us.userdata[UserDataEnum.MAX_WORLD_BOSS_HURT] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_WORLD_BOSS_HURT] = set.getInt(index++);
            index++;//属性
            int[] eggQuality = JkTools.readArray(set.getString(index++),",");
            if(eggQuality != null){
                for(int i=0;i<eggQuality.length-1;){
                    ByteIntPVo byteIntPVo = new ByteIntPVo();
                    i += EggQualityPVoJoin.instance.fromSplitStr(byteIntPVo,eggQuality,i);
                    us.eggQualityMap.put(byteIntPVo.key,byteIntPVo);
                }
            }
            us.giftCodeModel.loadData(set.getString(index++));
            index++;
            index++;
            us.userdata[UserDataEnum.OPEN_EQUIP_BAG_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.EQUIP_BAG_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.MR_MINE_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.MR_EXPLOIT_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.MR_SEARCH_COUNT] = set.getInt(index++);
            index++;
            us.userdata[UserDataEnum.MR_LOOT_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.MR_FREE_RECRUIT_COUNT] = set.getInt(index++);
            index++;
            us.userdata[UserDataEnum.LotteryPropFreeCount] = set.getByte(index++);
            us.userdata[UserDataEnum.LotteryStoneCount] = set.getByte(index++);
            us.userdata[UserDataEnum.LotteryDragonCount] = set.getByte(index++);
            us.userdata[UserDataEnum.LotteryStoneNextTime] = set.getInt(index++);
            us.userdata[UserDataEnum.LotteryDragonNextTime] = set.getInt(index++);
            index++;
            us.userdata[UserDataEnum.SNATCH_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.SNATCH_BUY_COUNT] = set.getInt(index++);
            us.userdata[UserDataEnum.TALENT_EXP] = set.getInt(index++);
            us.userdata[UserDataEnum.LJ_LOTTERY_COUNT] = set.getInt(index++);
            us.emperorModel.loadData(set.getString(index++));
            us.realmModel.loadData(set.getString(index++));
            client.currentUser = us;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (set != null) {
                try {
                    set.getStatement().close();
                    set.close();
                    closeSetConn();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }


        return true;
    }

//    private void loadFightData(User us, int[] tempAry) {
//
//if(tempAry.length<3)return ;
//        us.fightingLoot=new FightLootVo();
//    us.fightingLoot.mapID=tempAry[0];
//    us.fightingLoot.totalExp=tempAry[1];
//     us.fightingLoot.totalProps=new ArrayList<>();
//    for(
//    int i = 2;
//    i<tempAry.length-1;i+=3)
//
//    {
//        PropPVo pvo = new PropPVo();
//        pvo.tempID = tempAry[i];
//        pvo.baseID = tempAry[i + 1];
//        pvo.count = tempAry[i + 2];
//        us.fightingLoot.totalProps.add(pvo);
//    }
//
//}
//    public  boolean saveFightData(User user){
//
//        if(user.actState== UserActState.CAMP){
//             update(user,   FIELD_FIGHT,"null");
//        }else{
//            StringBuilder data=new StringBuilder();
//            data.append("\'");
//            data.append(user.fightingLoot.mapID);
//            data.append(",");
//            data.append(user.fightingLoot.totalExp);
//            data.append(",");
//
//            for (PropPVo pvo :  user.fightingLoot.totalProps ){
//                data.append(pvo.tempID);
//                data.append(",");
//                data.append(pvo.baseID);
//                data.append(",");
//                data.append(pvo.count);
//                data.append(",");
//            }
//            data.append("\'");
//            update(user,FIELD_FIGHT,data.toString());
//
//        }
//        return true;
//    }
 	public boolean insertNew(User user) {

long guid=AllSql.userSql.getNewGuid();
       insert(FIELD_GUID+","+FIELD_PASSPORT+","+FIELD_BASEID+","+FIELD_CREATED_TIME,guid+ ",\'"+user.client.passportVo.guid+"\',"+user.baseID+   ","+ user.userdata[UserDataEnum.RoleCreatTime]);

 		user.guid =guid;
 		return true;
	}

 	public void update(User user,String keyName,int value) {
 		  update(user.guid,keyName,value);
	}
    public void update(User user,String keyName,String value) {
        update(user.guid,keyName,value);
    }
    public void update(long guid,String keyName,int value) {
        update(keyName, value+"", guid);
    }
    public void update(long guid,String keyName,String value) {
        update(keyName, value, guid);
    }



}
