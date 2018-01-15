package base;

import comm.Model;
import comm.User;
import friend.FriendModel;
import gang.Gang;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.utils.JkTools;
import mail.LoopSendMailModel;
import protocol.*;
import redness.RednessModel;
import sendMsg.ClosedTestModel;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by admin on 2017/4/15.
 */
public class UserUpdateModel {
    public static HashMap<Integer,ArrayList<UserUpdateBaseVo>> userUpdateMap;
    public int lastUpdateTime;
    public User user;

    public UserUpdateModel(User user){
        this.user = user;
    }

    public void init() {
        user.activationModel.completedList.clear();
        user.activationModel.acceptedList.clear();
        int level = user.cacheUserVo.level;
        for (MissionBaseVo msvo : Model.MissionBaseMap.values()) {
            if (msvo.type == MissionTypeEnum.DAILY && msvo.showLevel <= level && (msvo.dropLevel ==0 || msvo.dropLevel > level)) {
                user.activationModel.acceptedList.put(msvo, 0);
            }
        }
        user.activationModel.saveSqlData();
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)user.cacheUserVo.passportVo.vip);
        if(vipBaseVo==null)return;
        for (UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()) {
            if(vo.dataType != 1)continue;
            if(vo.init>0){
                user.setUserData((byte) vo.dataID,vo.init, true);
            }else{
                switch (vo.dataID){
                    case UserDataEnum.WormNestResetTime :
                        user.setUserData((byte) vo.dataID,vipBaseVo.duchongMapReset, true);
                        break;
                    case UserDataEnum.DRAGON_STONE_BUY_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.dragonStoneCount, true);
                        break;
                    case UserDataEnum.EXCHANGE_TILI_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.buyTili, true);
                        break;
                    case UserDataEnum.EXCHANGE_ARENA_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.arenaBuyCount, true);
                        break;
                    case UserDataEnum.NORMAL_RESET_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.commMapReset, true);
                        break;
                    case UserDataEnum.MONEY_TREE_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.moneytreeCount, true);
                        break;
                    case UserDataEnum.ELITE_BUY_COUNT :
                        user.setUserData((byte) vo.dataID,vipBaseVo.eliteByCount, true);
                        break;
                    case UserDataEnum.LotteryPropFreeCount:
                        user.setUserData((byte) vo.dataID,vipBaseVo.lotteryPropCount,true);
                        break;
                }
            }
        }
        user.setUserData(UserDataEnum.WormNestCurrentFloor,1,true);
        user.friendModel.getRecommend(true);
        user.friendModel.canPickCount = (byte) Model.GameSetBaseMap.get(42).intArray[0];
        user.friendModel.canReceiveCount = (byte) Model.GameSetBaseMap.get(43).intArray[0];
        user.cacheUserVo.friendCacheModel.canPickedCount = (byte) Model.GameSetBaseMap.get(42).intArray[2];
        user.friendModel.saveSqlData();
        lastUpdateTime = JkTools.getGameServerTime(null);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_LAST_UPDATE_TIME,lastUpdateTime);
    }

    public static void modelInit(){
        userUpdateMap = new HashMap<>();
        for(UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()){
            if(userUpdateMap.containsKey(vo.minute)){
                ArrayList<UserUpdateBaseVo> list = userUpdateMap.get(vo.minute);
                list.add(vo);
            }else{
                ArrayList<UserUpdateBaseVo> list = new ArrayList<>();
                list.add(vo);
                userUpdateMap.put(vo.minute,list);
            }
            if(vo.type == 2){
                vo.typeAtrribute = vo.typeAtrribute%7 + 1;
            }
        }
    }

    public void update(UserUpdateBaseVo vo,boolean isOnline){
        switch (vo.dataType){
            case 1:
                int dailyAddMax=user.getUserVoAddMax(vo);
                if (vo.dailyAdd > 0) {
                    int oldVal = user.getUserData((byte) vo.dataID);
                    if (oldVal >= vo.max) break;

                    int canGet = vo.dailyAdd;
                    if (canGet > vo.max - oldVal) {
                        canGet = vo.max - oldVal;
                    }
                    if (dailyAddMax >= 0) {
                        if (oldVal >=dailyAddMax)
                            break;
                        if (canGet > dailyAddMax - oldVal) {
                            canGet =dailyAddMax - oldVal;
                        }
                    }
                    if (canGet <= 0) break;
                    user.setUserData((byte) vo.dataID, canGet + oldVal, true);
                }else if(dailyAddMax == -2){
                    user.setUserData((byte) vo.dataID, 0, true);
                }
                break;
            case 2:
                GangVo gangVo = user.cacheUserVo.gang.gangVo;
                if(gangVo == null)break;
                GangUserVo gangUserVo = gangVo.users.get(user.guid);
                if(gangUserVo == null)break;
                int AddMax=user.getGangUserVoAddMax(vo);
                if (vo.dailyAdd > 0) {
                    int oldVal = gangUserVo.getGangUserData((byte) vo.dataID);
                    if (oldVal >= vo.max) break;

                    int canGet = vo.dailyAdd;
                    if (canGet > vo.max - oldVal) {
                        canGet = vo.max - oldVal;
                    }
                    if (AddMax >= 0) {
                        if (oldVal >=AddMax)
                            break;
                        if (canGet > AddMax - oldVal) {
                            canGet =AddMax - oldVal;
                        }
                    }
                    if (canGet <= 0) break;
                    gangUserVo.setGangUserData((byte) vo.dataID, canGet + oldVal, true);
                }else if(AddMax == -2){
                    gangUserVo.setGangUserData((byte) vo.dataID, 0, true);
                }
                break;
            case 3:
                operate(vo.ID,isOnline);
                break;
        }
    }

    public void operate(int id,boolean isOnline){
        switch (id){
            case 101:
                user.activationModel.completedList.clear();
                user.activationModel.acceptedList.clear();
                int level = user.cacheUserVo.level;
                for (MissionBaseVo msvo : Model.MissionBaseMap.values()) {
                    if (msvo.type == MissionTypeEnum.DAILY && msvo.showLevel <= level && (msvo.dropLevel ==0 || msvo.dropLevel > level)) {
                        user.activationModel.acceptedList.put(msvo, 0);
                    }
                }
                if(isOnline) {
                    for (MissionBaseVo missionBaseVo : user.activationModel.acceptedList.keySet()) {
                        new MissionProcessRspd(user.client, missionBaseVo.ID, user.activationModel.acceptedList.get(missionBaseVo).shortValue());
                    }
                }
                user.activationModel.saveSqlData();
                break;
            case 102:
                Calendar calendar = JkTools.getCalendar();
                calendar.add(Calendar.HOUR_OF_DAY,user.client.addHours);
                int day = calendar.get(Calendar.DAY_OF_YEAR);
                int today = day+ JkTools.getBaseDay(calendar.get(Calendar.YEAR));
                HashSet<Long> set = new HashSet<>();
                for(MailPVo mailPVo : user.mailModel.unReadMailMap.values()){
                    if(today>=mailPVo.deadDay){
                        set.add(mailPVo.guid);
                        if(mailPVo.receiverID > 0){
                            AllSql.mailSql.update(mailPVo,AllSql.mailSql.FIELD_FLAG,1);
                        }
                    }
                }
                for(long i : set){
                    user.mailModel.unReadMailMap.remove(i);
                }
                set = new HashSet<>();
                for(MailPVo mailPVo : user.mailModel.readMailMap.values()){
                    if(today>=mailPVo.deadDay){
                        set.add(mailPVo.guid);
                        if(mailPVo.receiverID > 0){
                            AllSql.mailSql.update(mailPVo,AllSql.mailSql.FIELD_FLAG,1);
                        }
                    }
                }
                for(long i : set){
                    user.mailModel.readMailMap.remove(i);
                }
                break;
            case 103:
                if(user.hadGetTime < ClosedTestModel.CAN_GET_TIME && user.isGetCloseTest != 0){
                    user.isGetCloseTest = 0;
                    AllSql.userSql.update(user,AllSql.userSql.FIELD_IS_GET_CLOSE_TEST,0);
                    if(isOnline){
                        new ClosedTestTimeRspd(user.client,(byte)0,user.hadGetTime);
                    }
                }
                break;
            case 104:
                if(user.mapEnteredMap.size() > 0){
                    user.mapEnteredMap.clear();
                    AllSql.userSql.update(user,AllSql.userSql.FIELD_MAPENTERED,"'"+ MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values())+"'");
                    if(isOnline) {
                        new MapEnterTimesRspd(user.client,user.mapEnteredMap.values());
                    }
                }
                break;
            case 105:
                break;
            case 106:
                user.friendModel.getRecommend(true);
                break;
            case 107:

                for (PropShopPVo propShopPVo : user.propModel.propShopMap.values()) {
                    PropShopBaseVo vo = Model.PropShopBaseMap.get((int)propShopPVo.type).get(0);
                    int[] arr = null;
                    if(vo.type == 1){
                        arr = Model.GameSetBaseMap.get(13).intArray;
                    }
                    if(vo.type == 2){
                        arr = Model.GameSetBaseMap.get(46).intArray;
                    }
                    propShopPVo.freeTimes = (byte) arr[propShopPVo.type-(vo.type-1)*10-1];
                    propShopPVo.flushCount = 0;
                }
                user.propModel.saveSqlData();
                if(isOnline){
                    new RandomPropShopRspd(user.client,user.propModel.propShopFlushCD,user.propModel.propShopMap.values());
                }
                break;
            case 108:
                break;
            case 109:
                user.client.passportVo.loginTime = 0;
                AllSql.passportSql.update(user.client.passportVo,AllSql.passportSql.FIELD_LOGIN_TIME,0);
                break;
            case 110:
                GangVo gangVo = user.cacheUserVo.gang.gangVo;
                if (gangVo == null)break;
                if(gangVo.users.containsKey(user.guid)){
                    GangUserVo userVo = gangVo.users.get(user.guid);
                    if(userVo == null)break;
                    userVo.likeTime = (byte) Model.GameSetBaseMap.get(9).intArray[2];
                    AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_LIKE_TIME,userVo.likeTime);

                    if(isOnline) {
                        gangVo.createGangInfoRspd(user.client);
                    }
                }
                break;
            case 111:
//                if(user.worldBossModel.buffSet.size() > 0){
//                    user.worldBossModel.buffSet.clear();
//                }
                break;
            case 112:
                break;
            case 113:
                GangVo gVo = user.cacheUserVo.gang.gangVo;
                if (gVo == null)break;
                for(GangBuildPVo pVo : gVo.gangBuildMap.values()){
                    pVo.likes = 0;
                    pVo.discount = 100;
                }
                gVo.saveGangBuildInfo();
                gVo.lastResetDay = 1;
                AllSql.gangSql.update(gVo,AllSql.gangSql.FIELD_LAST_RESET_DAY,gVo.lastResetDay);
                if(isOnline){
                    gVo.createGangBuildInfoRspd(user.client);
                }
                break;
            case 114:
                GangVo gang = user.cacheUserVo.gang.gangVo;
                if (gang == null)break;
                if(gang.users.size()>1) {
                    if (JkTools.getGameServerDaysWith(null,gang.master.cacheUserVo.lastLoginTime) >= 5) {
                        gang.master.office = GangOfficeEnum.MEMBER;
                        gang.master.cacheUserVo.rankGangUserVo.office = GangOfficeEnum.MEMBER;
                        AllSql.gangMemberSql.update(gang.master, AllSql.gangMemberSql.FIELD_OFFICE, GangOfficeEnum.MEMBER);
                        gang.master.cacheUserVo.gangStatus = (byte) (3 + GangOfficeEnum.MEMBER);
                        AllSql.userSql.update(gang.master.cacheUserVo.guid, AllSql.userSql.FIELD_GANG_STATUS, gang.master.cacheUserVo.gangStatus);
                        gang.rankGangUserList.sortItem(gang.master.cacheUserVo.rankGangUserVo, gang.master.cacheUserVo.rankGangUserVo.orderScore());

                        GangUserVo gangUserVo = Gang.nextMaster(gang,gang.master.cacheUserVo.guid);
                        gangUserVo.office = GangOfficeEnum.MASTER;
                        gangUserVo.cacheUserVo.rankGangUserVo.office = GangOfficeEnum.MASTER;
                        gang.master = gangUserVo;
                        AllSql.gangMemberSql.update(gang.master, AllSql.gangMemberSql.FIELD_OFFICE, GangOfficeEnum.MASTER);
                        gang.addLog((short) 1005, gangUserVo.cacheUserVo.name);
                        gangUserVo.cacheUserVo.gangStatus = (byte) (3 + GangOfficeEnum.MASTER);
                        AllSql.userSql.update(gangUserVo.cacheUserVo.guid, AllSql.userSql.FIELD_GANG_STATUS, gangUserVo.cacheUserVo.gangStatus);
                        gang.rankGangUserList.sortItem(gangUserVo.cacheUserVo.rankGangUserVo, gangUserVo.cacheUserVo.rankGangUserVo.orderScore());
                    }
                }
                break;
            case 115:
                break;
            case 116:
                break;
            case 117:
                FriendModel friendModel = user.friendModel;
                friendModel.giveUser.clear();
                friendModel.pickUser.clear();
                friendModel.canPickCount = (byte) Model.GameSetBaseMap.get(42).intArray[0];
                friendModel.canReceiveCount = (byte) Model.GameSetBaseMap.get(43).intArray[0];
                friendModel.saveSqlData();
                break;
            case 118:
                user.getTili = "";
                AllSql.userSql.update(user,AllSql.userSql.FIELD_GET_TILI,"'"+user.getTili+"'");
                break;
            case 119:
                AllSql.userSql.update(user, AllSql.userSql.FIELD_WEEK_REDNESS_MONEY,0);
                if(isOnline){
                    if(RednessModel.campUserPVo != null){
                        HashMap<Long,Integer> rankMap = LoopSendMailModel.rankUsers.get(RednessModel.lastMailID);
                        int myLastIndex = -1;
                        if(rankMap != null && rankMap.containsKey(user.guid))myLastIndex = rankMap.get(user.guid);
                        new RP_LastRednessWinnerRspd(user.client,RednessModel.campUserPVo,RednessModel.lastRednessRank,myLastIndex);
                    }else{
                        CampUserPVo campUserPVo = new CampUserPVo();
                        campUserPVo.avata = new AvatarPVo();
                        new RP_LastRednessWinnerRspd(user.client,campUserPVo,RednessModel.lastRednessRank,-1);
                    }
                }
                break;
            case 120:
                user.cacheUserVo.mineModel.used.clear();
                user.cacheUserVo.mineModel.saveSqlData();
                break;
        }
    }


    public void checkUpdate() {
        int time = JkTools.getGameServerTime(user.client) - lastUpdateTime;
        boolean dayUpdate = false;
        boolean weekUpdate = false;
        if (time > Model.ONE_WEEK_TIME) {
            weekUpdate = true;
        }
        if (time > Model.ONE_DAY_TIME) {
            dayUpdate = true;
        }
        Calendar calendar = JkTools.getCalendar();
        calendar.add(Calendar.HOUR_OF_DAY,user.client.addHours);
        int nowDay = calendar.get(Calendar.DAY_OF_YEAR);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int minute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

        user.nextUpdataTime = nextUpdateTime(minute);

        Calendar lastCalendar = JkTools.getCalendar();
        lastCalendar.setTimeInMillis(((long)lastUpdateTime + 1451491200) * 1000);
        int lastDay = lastCalendar.get(Calendar.DAY_OF_YEAR);
        int lastWeekDay = lastCalendar.get(Calendar.DAY_OF_WEEK);
        int lastMinute = lastCalendar.get(Calendar.HOUR_OF_DAY) * 60 + lastCalendar.get(Calendar.MINUTE);
        for (int key : UserUpdateModel.userUpdateMap.keySet()) {
            ArrayList<UserUpdateBaseVo> list = UserUpdateModel.userUpdateMap.get(key);
            for (UserUpdateBaseVo vo : list) {
                switch (vo.type) {
                    case 1://每天
                        if(!dayUpdate){
//                            if(nowDay == lastDay){
//                                if(vo.minute <= lastMinute || vo.minute > minute)continue;
//                            }else{
//                                if(vo.minute >= lastMinute || vo.minute < minute)continue;
//                            }
                            if(nowDay == lastDay){
                                if(vo.minute <= lastMinute || vo.minute > minute)continue;
                            }else if(nowDay - lastDay == 1){
                                if(minute < lastMinute){
                                    if(vo.minute <= lastMinute && vo.minute > minute)continue;
                                }
                            }
                        }
                        break;
                    case 2://每周
                        if (!weekUpdate) {
                            int week = vo.typeAtrribute;
                            if (lastWeekDay > week) {
                                week += 7;
                            }
                            if (lastWeekDay > weekDay) {
                                weekDay += 7;
                            }
                            if (week > weekDay) continue;
                            if(nowDay == lastDay){
                                if(vo.minute <= lastMinute || vo.minute > minute)continue;
                            }else{
                                if(vo.minute >= lastMinute || vo.minute < minute)continue;
                            }
                            if (weekDay != vo.typeAtrribute) continue;
                        }
                        break;
                }
                update(vo,false);
            }
        }
        lastUpdateTime = JkTools.getGameServerTime(user.client);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_LAST_UPDATE_TIME,lastUpdateTime);
    }

    public static int nextUpdateTime(int now){
        int nextMinute = -1;
        int min = -1;
        for(int minute : userUpdateMap.keySet()){
            if(now < minute){
                if(nextMinute == -1){
                    nextMinute = minute;
                    continue;
                }
                if(nextMinute > minute){
                    nextMinute = minute;
                }
            }
            if(min == -1){
                min = minute;
            }else if(min > minute){
                min = minute;
            }
        }
        System.out.println("=======================nextMinute : "+nextMinute+"============================min : "+min);
        if(nextMinute == -1) return min;
        return nextMinute;
    }

    public void update(int minute, boolean isOnline) {
        user.nextUpdataTime = nextUpdateTime(minute);
        if (!UserUpdateModel.userUpdateMap.containsKey(minute)) return;
        ArrayList<UserUpdateBaseVo> list = UserUpdateModel.userUpdateMap.get(minute);
        Calendar calendar = JkTools.getCalendar();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (UserUpdateBaseVo vo : list) {
            switch (vo.type) {
                case 1://每天
                    break;
                case 2://每周
                    if (weekDay != vo.typeAtrribute) continue;
                    break;
                case 3://每月
                    if (vo.typeAtrribute == 99) {
                        if (monthDay != calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) continue;
                    } else {
                        if (monthDay != vo.typeAtrribute) continue;
                    }
                    break;
            }
            update(vo,isOnline);
        }
        lastUpdateTime = JkTools.getGameServerTime(user.client);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_LAST_UPDATE_TIME,lastUpdateTime);
    }
}
