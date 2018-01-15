package mission;

import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import protocol.*;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by jackie on 14-5-2.
 */
public class MissionModel extends BaseBlobDeal {
    public byte msType;
    public boolean isEnd = false;
    public int startTime;
    public HashMap<Integer,Integer> propMap = new HashMap<>();
    public HashMap<MissionBaseVo, Integer> acceptedList = new HashMap<>();
    public HashSet<Integer> completedList = new HashSet<>();
    public static HashMap<Integer,ActivationBaseVo> limitTimeActivation = new HashMap<>();

    public MissionModel(User user) {
        this.user = user;
    }

    public MissionModel(User user, byte msType) {
        this.user = user;
        this.msType = msType;
    }

    public static void initLimitTime(){
        for(Map.Entry<Integer,ArrayList<ActivationBaseVo>> item: Model.ActivationBaseMap.entrySet()){
            ActivationBaseVo activationBaseVo = item.getValue().get(0);
            if(activationBaseVo == null || activationBaseVo.params1 == null || activationBaseVo.params1[0] != -1)continue;
            limitTimeActivation.put(activationBaseVo.ID,activationBaseVo);
        }
    }
    //    public boolean progressBuyKill (MapBaseVo mapVo) {
//
//        for (MissionBaseVo msVo : acceptedList.keySet()) {
//
//            if (msVo.conditionType!=MissionConditionEnum.SURVEY&&msVo.conditionType!=MissionConditionEnum.KILLMONSTER){
//                continue;
//            }
//            if(mapVo.ID!=msVo.goMap){continue;}
//if(canComplete(msVo))continue;
//            short current= acceptedList.get(msVo).shortValue();
//            boolean changed=false;
//            if (msVo.conditionType==MissionConditionEnum.SURVEY){
//                     current=1;
//                    changed=true;
//
//
//            }else{
//                ArrayList<NpcLayoutBaseVo> npcList = Model.NpcLayoutBaseMap.get(mapVo.ID);
//
////                 if(npcList!=null)
////                    for (NpcLayoutBaseVo npc : npcList) {
////                        if(npc.npcID==msVo.condition[0]){
////                            current+=npc.pos.length/3;
////                            changed=true;
////                            if(current>=msVo.condition[1]){
////                                current=(short)msVo.condition[1];
////                                break;
////                            }
////                        }
////                    }
//            }
//
//
//            if(changed){
//                acceptedList.put(msVo,(int)current);
//                new MissionProcessRspd(user.client,msVo.ID, current);
//                AllSql.userSql.update(user, AllSql.userSql.FIELD_MISSION, user.missionModel.saveData());
//
//            }
//        }
//        return true;
//    }
    public boolean progressAdd(MissionBaseVo msVo) {

        short current = acceptedList.get(msVo).shortValue();
        int max = 1;
        if (msVo.completeCount > 1) max = msVo.completeCount;

        if (current < max) {
            current++;
            acceptedList.put(msVo, (int) current);
            new MissionProcessRspd(user.client, msVo.ID, current);
            AllSql.userSql.update(user, AllSql.userSql.FIELD_MAIN_MISSION, user.missionModel.saveData());

            return true;

        }
        return false;


    }

    public void progressBuyAct(byte missionCondition, int sub) {
        progressBuyAct(missionCondition, sub, 0);
    }

    public void progressBuyAct(byte missionCondition, int sub1, int sub2) {

        for (MissionBaseVo msVo : acceptedList.keySet()) {
            if (msVo.conditionType != missionCondition) continue;
            if (canCompleteParams(msVo, sub1, sub2) == false) continue;

            short current = acceptedList.get(msVo).shortValue();
            int max = 1;
            if (msVo.completeCount > 1) max = msVo.completeCount;

            if (current < max) {
                current++;
                acceptedList.put(msVo, (int) current);
                new MissionProcessRspd(user.client, msVo.ID, current);
                if (this == user.missionModel) {
                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAIN_MISSION, user.missionModel.saveData());
                }else if(this == user.activationModel){
                    AllSql.userSql.update(user, AllSql.userSql.FIELD_DAILY_MISSION, user.activationModel.saveData());
                }

                //return   ;

            }
        }


    }

    //    public boolean progressBuyCollect (int itemID,int itemCount) {
//
//        for (MissionBaseVo msVo : acceptedList.keySet()) {
//            if(msVo.conditionType!=MissionConditionEnum.COLLECT||msVo.condition[0]!=itemID)continue;
//            short current= acceptedList.get(msVo).shortValue();
//           if(current>=msVo.condition[1])return true;
//
//            current+=itemCount;
//
//            if(current>=msVo.condition[1]){
//                current=(short)msVo.condition[1];
//
//            }
//            acceptedList.put(msVo,(int)current);
//            new MissionProcessRspd(user.client,msVo.ID, current);
//            AllSql.userSql.update(user, AllSql.userSql.FIELD_MISSION, user.missionModel.saveData());
//
//        }
//        return true;
//    }
    public void accept(int msID) {
        MissionBaseVo baseVo = Model.MissionBaseMap.get(msID);
        acceptedList.put(baseVo, 0);
        new MissionAcceptRspd(user.client, msID);


    }

    public void calculateNewMission() {
        for (MissionBaseVo msVo : Model.MissionBaseMap.values()) {
            if (msVo.type != msType) continue;
            if (canAccept(msVo) == false) continue;
            accept(msVo.ID);
        }
        if(this == user.activationModel){
            AllSql.userSql.update(user, AllSql.userSql.FIELD_ACHIEVE_MISSION, saveData());
        }
    }

    public boolean canAccept(MissionBaseVo msVo) {

        if (completedList.contains(msVo.ID) || acceptedList.containsKey(msVo)) return false;
        if (msVo.showLevel > user.getUserData(UserDataEnum.LEVEL)) return false;
        if (msVo.dropLevel > 0 && msVo.dropLevel <= user.getUserData(UserDataEnum.LEVEL)) {
            return false;
        }
        if (msVo.needMission > 0 && !user.missionModel.completedList.contains(msVo.needMission)) return false;
        return true;
    }

    public int canComplete(MissionBaseVo missionBaseVo) {
        if (completedList.contains(missionBaseVo.ID)) return -2;//已完成
        if(missionBaseVo.conditionType>100000)return 0;
        if (missionBaseVo.conditionType > 10000) {
            int value = user.getUserData((byte) (missionBaseVo.conditionType - 10000));
            return canCompleteParams(missionBaseVo, value, 0) ? -1:value;
        } else if (msType == MissionTypeEnum.MAIN || msType == MissionTypeEnum.DAILY) {
            if (acceptedList.containsKey(missionBaseVo) == false) return 0;

            int progress = acceptedList.get(missionBaseVo);

            return progress >= missionBaseVo.completeCount?-1:progress;
        } else {
            switch (missionBaseVo.conditionType) {
                case MissionConditionEnum.CHAPTER_STARS:
                    int chapterStars = 0;
                    int difficulty = missionBaseVo.conditionParams[1]/100;
                    int questID = missionBaseVo.conditionParams[1]%100;
                    for (MapQualityPVo item : user.mapQualityMap.values()) {
                        MapBaseVo mapBaseVo = Model.MapBaseMap.get(item.ID);
                        if(mapBaseVo == null)continue;
                        if(mapBaseVo.difficulty != difficulty)continue;
                        if(mapBaseVo.questID != questID)continue;
                        chapterStars += item.stars.size();
                    }
                    return canCompleteParams(missionBaseVo, missionBaseVo.conditionParams[1], chapterStars)?-1:chapterStars;
                case MissionConditionEnum.GIFT_7DAY:
                    return canCompleteParams(missionBaseVo, (JkTools.getGameServerTime(user.client)-startTime)/Model.ONE_DAY_TIME+1,0)?-1:0;
                case MissionConditionEnum.SKILL_LEVEL:
                    int level = user.cacheUserVo.skillModel.skills.get(missionBaseVo.connetExtraID).level;
                    return canCompleteParams(missionBaseVo, level, 0)?-1:level;
                case MissionConditionEnum.ACTIVATE_SKIN:
                    if (user.cacheUserVo.skinModel.skinMap.containsKey((short)missionBaseVo.conditionParams[1]))
                        return canCompleteParams(missionBaseVo, missionBaseVo.conditionParams[1], 0)?-1:0;
                    break;
                case MissionConditionEnum.JION_GANG:
                    if (user.cacheUserVo.gang.gangVo != null)
                        return user.cacheUserVo.gang.gangVo.gangID > 0?-1:0;
                    break;
                case MissionConditionEnum.SURVEY:
                    if (user.mapQualityMap.containsKey(missionBaseVo.conditionParams[1]))
                        return canCompleteParams(missionBaseVo, missionBaseVo.conditionParams[1], 1)?-1:0;
                    break;
                case MissionConditionEnum.DRAGON_TYPE:
                    if (user.cacheUserVo.dragonCacheModel.dragonsMap.containsKey((short)missionBaseVo.conditionParams[1]))
                        return canCompleteParams(missionBaseVo, missionBaseVo.conditionParams[1], 0)?-1:0;
                    break;
                case MissionConditionEnum.DRAGON_STONE_LEVEL:
                    if (user.dragonStoneOwnLevel.indexOf(String.valueOf(missionBaseVo.conditionParams[1])) != -1)
                        return canCompleteParams(missionBaseVo, missionBaseVo.conditionParams[1], 0)?-1:0;
                    break;
                case MissionConditionEnum.DRAGON_STONE_ZDL:
                    return canCompleteParams(missionBaseVo, user.dragonStoneZDL, 0)?-1:user.dragonStoneZDL;
                case MissionConditionEnum.ZDL:
                    return canCompleteParams(missionBaseVo, user.cacheUserVo.zdl, 0)?-1:user.cacheUserVo.zdl;
                case MissionConditionEnum.RECHARGE_DIAMON:
                    int diamond = user.cacheUserVo.passportVo.diamond;
                    return canCompleteParams(missionBaseVo, diamond, 0)?-1:diamond;
                case MissionConditionEnum.EQUIP_QUALITY:
                    byte quality = (byte)missionBaseVo.conditionParams[1];
                    if(user.equipQualityMap.containsKey(quality)){
                        ByteIntPVo byteIntPVo = user.equipQualityMap.get(quality);
                        return canCompleteParams(missionBaseVo,quality,byteIntPVo.value)?-1:byteIntPVo.value;
                    }
                    break;
                case MissionConditionEnum.CARNET_COUNT:
                    if(user.carnetCountMap.containsKey(missionBaseVo.conditionParams[1])){
                        IntIntPVo intIntPVo = user.carnetCountMap.get(missionBaseVo.conditionParams[1]);
                        return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],intIntPVo.value)?-1:intIntPVo.value;
                    }
                    break;
                case MissionConditionEnum.TALENT_COUNT:
                    int count = user.cacheUserVo.skillModel.talents.size();
                    return canCompleteParams(missionBaseVo, count, 0)?-1:count;
                case MissionConditionEnum.STONE_QUALITY:
                    byte stoneQuality = (byte)missionBaseVo.conditionParams[1];
                    if(user.stoneCountMap.containsKey(stoneQuality)){
                        ByteIntPVo byteIntPVo = user.stoneCountMap.get(stoneQuality);
                        return canCompleteParams(missionBaseVo,stoneQuality,byteIntPVo.value)?-1:byteIntPVo.value;
                    }
                    break;
                case MissionConditionEnum.LOTTERY_DRAGON_STONE:
                    int value = user.getUserData(UserDataEnum.LJ_DIAMOND_BUY_DRAGON_STONE_COUNT)+user.getUserData(UserDataEnum.LJ_MONEY_BUY_DRAGON_STONE_COUNT);
                    return canCompleteParams(missionBaseVo,value,0)?-1:value;
                case MissionConditionEnum.ACTIVATE_PROP_PROGRESS:
                    if(user.limitTimeMap.containsKey((byte)missionBaseVo.type)){
                        MissionModel msModel = user.limitTimeMap.get((byte)missionBaseVo.type);
                        if(msModel != null && msModel.propMap.containsKey(missionBaseVo.conditionParams[1])){
                            int propCount = msModel.propMap.get(missionBaseVo.conditionParams[1]);
                            return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],propCount)?-1:propCount;
                        }
                    }
                    break;
                case MissionConditionEnum.LJ_DIAMOND_COUNT:
                    int diamondCount = user.getUserData(UserDataEnum.DIAMOND)+user.getUserData(UserDataEnum.LJ_COST_DIAMOND_COUNT);
                    return canCompleteParams(missionBaseVo,diamondCount,0)?-1:diamondCount;
                case MissionConditionEnum.LJ_DRAGON_ACHIEVE_COUNT:
                    int dragonAchieveCount = user.dragonModel.dragonAchieveSet.size();
                    return canCompleteParams(missionBaseVo,dragonAchieveCount,0)?-1:dragonAchieveCount;
                case MissionConditionEnum.LJ_DRAGON_TALENT_COUNT:
                    int dragonTalentCount = 0;
                    for(Map.Entry<Short,ArrayList<DragonTalentPVo>> map : user.dragonModel.dragonTalentMap.entrySet()){
                        dragonTalentCount += map.getValue().size();
                    }
                    return canCompleteParams(missionBaseVo,dragonTalentCount,0)?-1:dragonTalentCount;
                case MissionConditionEnum.THE_DRAGON_SKILL_LEVEL:
                    int dragonSkillMaxLevel = 0;
                    for(DragonPVo dragonPVo : user.cacheUserVo.dragonCacheModel.dragonsMap.values()){
                        if(dragonPVo.skillLevel > dragonSkillMaxLevel){
                            dragonSkillMaxLevel = dragonPVo.skillLevel;
                        }
                    }
                    return canCompleteParams(missionBaseVo,dragonSkillMaxLevel,0)?-1:dragonSkillMaxLevel;
                case MissionConditionEnum.THE_DRAGON_LEVEL:
                    DragonPVo dragonPVo = user.cacheUserVo.dragonCacheModel.dragonsMap.get((short)missionBaseVo.conditionParams[1]);
                    if(dragonPVo != null && dragonPVo.isActive){
                        return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],dragonPVo.level)?-1:dragonPVo.level;
                    }
                    break;
                case MissionConditionEnum.HATCH_DRAGON_STONE_QUALITY:
                    byte eggQuality = (byte)missionBaseVo.conditionParams[1];
                    if(user.eggQualityMap.containsKey(eggQuality)){
                        ByteIntPVo byteIntPVo = user.eggQualityMap.get(eggQuality);
                        return canCompleteParams(missionBaseVo,eggQuality,byteIntPVo.value)?-1:byteIntPVo.value;
                    }
                    break;
                case MissionConditionEnum.DRAGON_ZDL:
                   int dragonZDL = user.dragonModel.getDragonZDL();
                    return canCompleteParams(missionBaseVo,dragonZDL,0)?-1:dragonZDL;
                case MissionConditionEnum.ACTIVATE_PROGRESS:
                    if(user.limitTimeMap.containsKey((byte)missionBaseVo.type)) {
                        MissionModel msModel = user.limitTimeMap.get((byte) missionBaseVo.type);
                        if(msModel != null){
                            int completeCount = msModel.completedList.size();
                            return canCompleteParams(missionBaseVo,completeCount,0)?-1:completeCount;
                        }
                    }
                    break;
                case MissionConditionEnum.DRAGON_COUNT:
                    int dragonCount=0;
                    for(DragonPVo item : user.cacheUserVo.dragonCacheModel.dragonsMap.values()){
                        if(item.isActive)dragonCount++;
                    }
                    return canCompleteParams(missionBaseVo,dragonCount,0)?-1:dragonCount;
                case MissionConditionEnum.THE_DRAGON_ADVANCE:
                    DragonPVo dragonAdvancePVo = user.cacheUserVo.dragonCacheModel.dragonsMap.get((short)missionBaseVo.conditionParams[1]);
                    if(dragonAdvancePVo != null && dragonAdvancePVo.isActive){
                        return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],dragonAdvancePVo.advance)?-1:dragonAdvancePVo.advance;
                    }
                    break;
                case MissionConditionEnum.VIP_LEVEL:
                    return canCompleteParams(missionBaseVo,user.cacheUserVo.passportVo.vip,0)?-1:user.cacheUserVo.passportVo.vip;
                case MissionConditionEnum.DRAGON_ADVANCE:
                    int advanceCount = 0;
                    for(DragonPVo item : user.cacheUserVo.dragonCacheModel.dragonsMap.values()){
                        if(item.isActive && item.advance == missionBaseVo.conditionParams[1])advanceCount++;
                    }
                    return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],advanceCount)?-1:advanceCount;
                case MissionConditionEnum.DRAGON_QUALITY:
                    int qualityCount = 0;
                    for(DragonPVo item : user.cacheUserVo.dragonCacheModel.dragonsMap.values()){
                        if(item.isActive && item.quality == missionBaseVo.conditionParams[1])qualityCount++;
                    }
                    return canCompleteParams(missionBaseVo,missionBaseVo.conditionParams[1],qualityCount)?-1:qualityCount;
            }
        }
        return 0;
}

    private boolean canCompleteParams(MissionBaseVo msVo, int sub1, int sub2) {
        if (msVo.conditionParams != null) {
            if (JkTools.compare(sub1, msVo.conditionParams[1], msVo.conditionParams[0]) == false) return false;
            if (msVo.conditionParams.length >= 4 && JkTools.compare(sub2, msVo.conditionParams[3], msVo.conditionParams[2]) == false)
                return false;
        }
        return true;
    }

    public boolean hasAcceptedMission(byte type) {
        for (MissionBaseVo msVo : acceptedList.keySet()) {
            if (msVo.type == type) return true;
        }
        return false;
    }

    public void clearDailyMission() {

//        for (MissionBaseVo msVo : acceptedList.keySet()) {
//if(msVo.type==MissionTypeEnum.DAILY){
//    acceptedList.remove(msVo);
//    return;
//}
//        }
    }
    public static boolean isActive(User user,int type){//限时活动是否启用
        LimitTimeActivationBaseVo vo = Model.LimitTimeActivationBaseMap.get(type);
        if(vo == null)return false;
        if(vo.triggerType == 0){//时间触发

        }else{//其他触发
            MissionModel missionModel = user.limitTimeMap.get((byte)type);
            if(missionModel == null || missionModel.startTime <= 0)return false;
            int now = JkTools.getGameServerTime(user.client);
            int out = missionModel.startTime + Integer.parseInt(vo.timeOut)*Model.ONE_DAY_TIME;
            if(JkTools.compare(now,out,JkTools.COMPARE_BIG)){
                return false;
            }
        }
        return true;
    }

    public static boolean isUpdateDate(User user,int type){//限时活动结束不更新任务
        LimitTimeActivationBaseVo vo = Model.LimitTimeActivationBaseMap.get(type);
        if(vo == null)return false;
        if(vo.triggerType == 0){//时间触发

        }else {//其他触发
            MissionModel missionModel = user.limitTimeMap.get((byte)type);
            if(missionModel == null || missionModel.startTime <= 0)return false;
            int now = JkTools.getGameServerTime(user.client);
            int end = missionModel.startTime + Integer.parseInt(vo.timeEnd)*Model.ONE_DAY_TIME;
            if(JkTools.compare(now,end,JkTools.COMPARE_BIG)){
                return false;
            }
        }
        return true;
    }

    public static void checkUpdate(User user) {
        if (user.limitTimeMap.size() > 0) {
            HashSet<Byte> set = new HashSet<>();
            MissionModel missionModel = null;
            for (MissionModel msModel : user.limitTimeMap.values()) {
                if (!MissionModel.isActive(user, msModel.msType)) {//限时活动过期
                    set.add(msModel.msType);
                    missionModel = msModel;
                }
                if (!msModel.isEnd && user.limitTimeMap.containsKey(msModel.msType) && !MissionModel.isUpdateDate(user, msModel.msType)) {//限时活动领取时间段不更新任务进度
                    for (MissionBaseVo vo : Model.MissionBaseMap.values()) {
                        if (vo.type != msModel.msType) continue;
                        if (msModel.completedList.contains(vo.ID)) continue;
                        int value = msModel.canComplete(vo);
                        if (value == -1 || value == -2) continue;
                        msModel.acceptedList.put(vo, value);
                    }
                    msModel.isEnd = true;
                    missionModel = msModel;
                }
            }
            if(set.size() >0){
                for(byte type: set){
                    user.limitTimeMap.remove(type);
                    user.timeOutSet.add(type);
                }
            }
            if(missionModel != null){
                missionModel.saveSqlData();
            }
        }
    }

    public static boolean compare(User user,String str,int compare){
        if(!"".equals(str)){
            if(str.length() == 1){
                if(JkTools.compare(user.getCreatedDays(),Integer.parseInt(str),compare))return true;
            }else{
                Date date = JkTools.getDate(str);
                Calendar calendar = JkTools.getCalendar();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                int time = JkTools.getGameServerTime(calendar.getTimeInMillis());
                if(JkTools.compare(time,JkTools.getGameServerTime(user.client),compare)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[acceptedList.size() * 5 + completedList.size() * 4 + 5];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(completedList.size());

        for (Integer id : completedList) {
            buffer.putInt(id);
        }

        buffer.put((byte) acceptedList.size());
        for (Map.Entry<MissionBaseVo, Integer> item : acceptedList.entrySet()) {
            buffer.putInt(item.getKey().ID);
            buffer.put(item.getValue().byteValue());
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if (buffer == null) return;
        int count = buffer.getInt();

        for (int i = 0; i < count; i++) {
            completedList.add(buffer.getInt());
        }

        count = buffer.get();
        for (int i = 0; i < count; i++) {

            int baseID = buffer.getInt();
            int progress = buffer.get();
            if(Model.MissionBaseMap.containsKey(baseID)){
                acceptedList.put(Model.MissionBaseMap.get(baseID), progress);
            }
        }
    }

    public static void sendLimitTimeInfo(User user){
        checkUpdate(user);
        ArrayList<LimitTimePVo> list = new ArrayList<>();
        ArrayList<ActivityTimePVo> timeList = new ArrayList<>();
        for(Map.Entry<Byte,MissionModel> item : user.limitTimeMap.entrySet()){
            LimitTimePVo limitTimePVo = new LimitTimePVo();
            MissionModel msModel = item.getValue();
            limitTimePVo.type = item.getKey();
            limitTimePVo.props = new ArrayList<>();
            limitTimePVo.count = new ArrayList<>();
            limitTimePVo.missionID = new ArrayList<>();
            limitTimePVo.process = new ArrayList<>();
            for(Map.Entry<Integer,Integer> prop : msModel.propMap.entrySet()){
                limitTimePVo.props.add(prop.getKey());
                limitTimePVo.count.add(prop.getValue());
            }
            if(msModel.isEnd == true){
                for(Map.Entry<MissionBaseVo,Integer> mission : msModel.acceptedList.entrySet()){
                    limitTimePVo.missionID.add(mission.getKey().ID);
                    limitTimePVo.process.add(mission.getValue());
                }
            }
            limitTimePVo.completedList = msModel.completedList;
            list.add(limitTimePVo);
            if(msModel.startTime > 0){
                LimitTimeActivationBaseVo vo = Model.LimitTimeActivationBaseMap.get((int)msModel.msType);
                ActivityTimePVo activityTimePVo = new ActivityTimePVo();
                activityTimePVo.type = msModel.msType;
                activityTimePVo.start = msModel.startTime;
                activityTimePVo.end = activityTimePVo.start + Integer.parseInt(vo.timeEnd) * Model.ONE_DAY_TIME;
                activityTimePVo.endGet = activityTimePVo.start + Integer.parseInt(vo.timeEndGet) * Model.ONE_DAY_TIME;
                activityTimePVo.destoy = activityTimePVo.start + Integer.parseInt(vo.timeOut) * Model.ONE_DAY_TIME;
                timeList.add(activityTimePVo);
            }
        }
        new LimitTimeActivityRspd(user.client,list);
        if(timeList.size()>0){
            new ActivateTimeListRspd(user.client,timeList);
        }
    }

    @Override
    public void saveSqlData() {
        switch (msType) {
            case MissionTypeEnum.MAIN:
                AllSql.userSql.update(user, AllSql.userSql.FIELD_MAIN_MISSION, saveData());
                break;
            case MissionTypeEnum.DAILY:
                AllSql.userSql.update(user, AllSql.userSql.FIELD_DAILY_MISSION, saveData());
                break;
            case MissionTypeEnum.ACHIEVE:
                AllSql.userSql.update(user, AllSql.userSql.FIELD_ACHIEVE_MISSION, saveData());
                break;
            default:
                if(msType > MissionTypeEnum.ACHIEVE){
                    saveLimitTime();
                }
                break;
        }

    }

    @Override
    public void unloadUser() {
        user = null;
    }

    private void saveLimitTime(){
        int size = 0;
        for(MissionModel msModel : user.limitTimeMap.values()){
            size += msModel.completedList.size()*4 + 12 +msModel.propMap.size()*8 + msModel.acceptedList.size()*8;
        }
        byte[] bytes = new byte[size+2+user.timeOutSet.size()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) user.limitTimeMap.size());
        for(MissionModel msModel : user.limitTimeMap.values()){
            buffer.put(msModel.msType);
            buffer.put((byte) (msModel.isEnd?1:0));
            buffer.putInt(msModel.startTime);
            buffer.putShort((short)msModel.completedList.size());
            buffer.putShort((short)msModel.propMap.size());
            buffer.putShort((short)msModel.acceptedList.size());
            for(Map.Entry<Integer,Integer> item : msModel.propMap.entrySet()){
                buffer.putInt(item.getKey());
                buffer.putInt(item.getValue());
            }
            for(int id : msModel.completedList){
                buffer.putInt(id);
            }
            for(Map.Entry<MissionBaseVo,Integer> item : msModel.acceptedList.entrySet()){
                buffer.putInt(item.getKey().ID);
                buffer.putInt(item.getValue());
            }
        }
        buffer.put((byte) user.timeOutSet.size());
        for(byte id : user.timeOutSet){
            buffer.put(id);
        }
        AllSql.userSql.update(user,AllSql.userSql.FIELD_LIMIT_TIME_ACTIVITY,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public static void loadLimitTime(User user,String str){
        if(str == null || str.length()<1)return;
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int count = buffer.get();
        for(int i=0;i<count;i++){
            MissionModel msModel = new MissionModel(user,buffer.get());
            msModel.isEnd = buffer.get() == 1;
            msModel.startTime = buffer.getInt();
            short completedSize = buffer.getShort();
            short propSize = buffer.getShort();
            short acceptedSize = buffer.getShort();
            for(int j=0;j<propSize;j++){
                msModel.propMap.put(buffer.getInt(),buffer.getInt());
            }
            for(int j=0;j<completedSize;j++){
                msModel.completedList.add(buffer.getInt());
            }
            for(int j=0;j<acceptedSize;j++){
                int missionID = buffer.getInt();
                if(Model.MissionBaseMap.containsKey(missionID)){
                    msModel.acceptedList.put(Model.MissionBaseMap.get(missionID),buffer.getInt());
                }
            }
            user.limitTimeMap.put(msModel.msType,msModel);
        }
        int size = buffer.get();
        for(int i=0;i<size;i++){
            user.timeOutSet.add(buffer.get());
        }
    }
}


