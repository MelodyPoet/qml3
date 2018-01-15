package talk;

import comm.Model;
import gluffy.utils.JkTools;
import protocol.TalkMsgPVo;
import sqlCmd.AllSql;
import table.WorldTreeBaseVo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static talk.TalkModel.LIMIT_COUNT;


/**
 * Created by admin on 2016/8/1.
 */
public class LoopSendTalkModel {
    public static Map<Integer,int[]> loopDayMap = new HashMap<>();
    public static Map<Integer,int[]> loopWeekMap = new HashMap<>();
    public static Map<Integer,int[]> loopMonthMap = new HashMap<>();
    public static ArrayList<TalkMsgPVo> systemTalkList = new ArrayList<>();
    public static int baseID = 100000;

    public static void init(){
        AllSql.worldTreeSql.loadSystemTalk();
        for(WorldTreeBaseVo vo : Model.WorldTreeBaseMap.values()){
            classify(vo);
        }
    }

    public static void classify(WorldTreeBaseVo vo){
        int day = JkTools.getCalendar().get(Calendar.DAY_OF_YEAR);
        if(vo.cycleType == 1){//每天(当day不为0时，表示某一天)
            if(vo.day < day && vo.day != 0){
                return;
            }
            int[] arr = new int[4];
            arr[0] = vo.ID;
            arr[1] = vo.day;
            arr[2] = vo.hours;
            arr[3] = vo.minutes;
            loopDayMap.put(vo.ID,arr);
        }
        if(vo.cycleType == 2){//每周
            int[] arr = new int[4];
            arr[0] = vo.ID;
            arr[1] = vo.day%7+1;
            arr[2] = vo.hours;
            arr[3] = vo.minutes;
            loopWeekMap.put(vo.ID,arr);
        }
        if(vo.cycleType == 3){//每月（day为99时表示每个月最后一天）
            int[] arr = new int[5];
            arr[0] = vo.ID;
            if(vo.day == 99){
                int max = JkTools.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
                arr[4] = 1;//每月最后一天类型的标识
                arr[1] = max;
            }else{
                arr[1] = vo.day;
            }
            arr[2] = vo.hours;
            arr[3] = vo.minutes;
            loopMonthMap.put(vo.ID,arr);
        }
    }

    public static void addSystemTalk(TalkMsgPVo talkMsgPVo, TalkRedPacketVo packetVo,boolean isAddTalk) {
        Calendar calendar = JkTools.getCalendar();
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        int time = JkTools.getGameServerTime(calendar.getTimeInMillis());
        for(TalkMsgPVo pVo : systemTalkList){
            if(pVo.roleID == talkMsgPVo.roleID){
                if(pVo.createTime - time <Model.ONE_HOURS_TIME){
                    return;
                }
            }
        }
        if(systemTalkList.size() >= LIMIT_COUNT){
            systemTalkList.remove(0);
        }
        if(isAddTalk){
            TalkModel.addTalkMsg(talkMsgPVo,packetVo,true);
            new GetTalkBackCmd().addSystemTalk();
        }
        systemTalkList.add(talkMsgPVo);
    }

    public static void loopSendTalk(){
        Calendar cal = JkTools.getCalendar();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        for(int[] arr : loopDayMap.values()){//每天或某一天
            if(arr[1] > 0){//某一天特殊处理
                if(day!=arr[1])continue;
            }
            if(hours!=arr[2])continue;
            if(minutes!=arr[3]%60)continue;
            if(arr[3]/60>0){
                arr[3] -= 61;
                continue;
            }
            arr[3] = arr[3] + 61;
            TalkModel.addTalkMsgBySystem(arr[0]);
            if(arr[1] > 0) {//某一天，执行完后移除
                loopDayMap.remove(arr[0]);
            }
        }
        for(int[] arr : loopWeekMap.values()){//每周
            if(dayOfWeek!=arr[1])continue;
            if(hours!=arr[2])continue;
            if(minutes!=arr[3]%60)continue;
            if(arr[3]/60>0){
                arr[3] -= 61;
                continue;
            }
            arr[3] = arr[3] + 61;
            TalkModel.addTalkMsgBySystem(arr[0]);
        }
        for(int[] arr : loopMonthMap.values()){//每月
            if(dayOfMonth!=arr[1])continue;
            if(hours!=arr[2])continue;
            if(minutes!=arr[3]%60)continue;
            if(arr[3]/60>0){
                arr[3] -= 61;
                continue;
            }
            arr[3] = arr[3] + 61;
            TalkModel.addTalkMsgBySystem(arr[0]);
            if(arr[4] == 1){
                Calendar calendar = JkTools.getCalendar();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
                arr[1] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        }
    }
}
