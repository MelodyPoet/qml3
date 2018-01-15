package base;

import arena.ArenaModel;
import comm.CacheUserVo;
import comm.Model;
import gang.Gang;
import gang.GangVo;
import gang.guess.GuessModel;
import gluffy.utils.JkTools;
import gluffy.utils.RankSortedList;
import mine.MineModel;
import rank.RankModel;
import redness.RednessLoopModel;
import table.SystemUpdateBaseVo;
import talk.TalkModel;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by admin on 2017/4/15.
 */
public class SystemLoopModel {
    public static HashMap<Integer, ArrayList<SystemUpdateBaseVo>> systemUpdateMap;
    public static int MINUTE = 24 * 60;
    public static int lastUpdateMunite;

    public static void init() {
        systemUpdateMap = new HashMap<>();
        for (SystemUpdateBaseVo vo : Model.SystemUpdateBaseMap.values()) {
            if(vo.dataType != 0)continue;
            if (systemUpdateMap.containsKey(vo.minute)) {
                ArrayList<SystemUpdateBaseVo> list = systemUpdateMap.get(vo.minute);
                list.add(vo);
            } else {
                ArrayList<SystemUpdateBaseVo> list = new ArrayList<>();
                list.add(vo);
                systemUpdateMap.put(vo.minute, list);
            }
            if(vo.type == 2){
                vo.typeAtrribute = vo.typeAtrribute%7 + 1;
            }
        }
        checkUpdate();
    }

    public static void loop() {
        Calendar calendar = JkTools.getCalendar();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int now = hours * 60 + minute;
        if(now == lastUpdateMunite)return;
        lastUpdateMunite = now;
        if (systemUpdateMap.containsKey(now)) {
            ArrayList<SystemUpdateBaseVo> list = systemUpdateMap.get(now);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
            for (SystemUpdateBaseVo vo : list) {
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
                update(vo.ID);
            }
        }
    }

    public static void update(int id) {
        switch (id) {
            case 1:
                break;
            case 2:
                for(GangVo gangVo : Gang.allGangList){
                    if(gangVo.giftMap.size() > 0){
                        gangVo.giftMap.clear();
                        gangVo.giftLogList.clear();
                        gangVo.saveGangGiftList();
                        gangVo.saveGangGiftLog();
                    }
                }
                break;
            case 3:
                GuessModel.openAll();
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                for(GangVo gangVo : Gang.allGangList){
                    gangVo.mapModel.clear();
                }
                break;
            case 9:
                for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
                    cacheUserVo.friendCacheModel.canPickedCount = (byte) Model.GameSetBaseMap.get(42).intArray[2];
                }
                break;
            case 10:
                //每天早上六点清空世界树
                TalkModel.rankLikeList=new RankSortedList<>();
                TalkModel.rankLikeMap.clear();
                TalkModel.talkMsgMap=new HashMap<>();
                TalkModel.talkMsgList=new ArrayList<>();
                TalkModel.redPacketMap=new HashMap<>();
                TalkModel.userLikesMap=new HashMap<>();
                TalkModel.systemMsgNum = 0;
                TalkModel.redPackMsgNum = 0;
                TalkModel.bossMsgNum = 0;
                TalkModel.lastOperateDay = JkTools.getCalendar().get(Calendar.DAY_OF_YEAR);
                break;
            case 11:
                ArenaModel.recordArenaReward();
                break;
            case 12:
                break;
            case 13:
                RednessLoopModel.recordRednessRank();
                break;
            case 14:
               // WorldBossModel.sendWBReward();
                break;
            case 19:
                MineModel.sendMineReward();
                break;
            case 20:
                RankModel.recordZDLRank();
                break;
        }
    }


    public static void checkUpdate() {
        Calendar calendar = JkTools.getCalendar();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int minute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        lastUpdateMunite = minute;
        for (int key : systemUpdateMap.keySet()) {
            ArrayList<SystemUpdateBaseVo> list = systemUpdateMap.get(key);
            for (SystemUpdateBaseVo vo : list) {
                if(vo.relevancyID == 0)continue;
                SystemUpdateBaseVo systemUpdateBaseVo = Model.SystemUpdateBaseMap.get(vo.relevancyID);
                switch (vo.type) {
                    case 1://每天
                        if(vo.relevancyID == -1)break;
                        if(vo.minute > minute)continue;
                        if(systemUpdateBaseVo.minute <= minute)continue;
                        break;
                    case 2://每周
                        int endWeekDay = systemUpdateBaseVo.typeAtrribute;
                        if(vo.typeAtrribute > weekDay){
                            weekDay += 7;
                        }
                        if(vo.typeAtrribute > endWeekDay){
                            endWeekDay += 7;
                        }
                        if(weekDay > endWeekDay)continue;
                        if(weekDay == vo.typeAtrribute && minute < vo.minute)continue;
                        if(weekDay == endWeekDay && minute > systemUpdateBaseVo.minute)continue;
                        break;
                }
                update(vo.ID);
            }
        }
    }
}
