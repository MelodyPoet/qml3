package airing;

import comm.Model;
import gluffy.utils.JkTools;
import table.AiringBaseVo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by admin on 2016/8/1.
 */
public class LoopAiringModel {
    public static Map<Integer,int[]> loopDayMap = new HashMap<>();
    public static Map<Integer,int[]> loopWeekMap = new HashMap<>();
    public static Map<Integer,int[]> loopMonthMap = new HashMap<>();
    public static int baseID = 100000;

    public static void init(){
        for(AiringBaseVo vo : Model.AiringBaseMap.values()){
            if(vo.times == -1){
                AiringModel.addSystemAiring(vo.ID);
            }
            classify(vo);
        }
    }

    public static void classify(AiringBaseVo vo){
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

    public static void loopSendAiring(){
        Calendar calendar = JkTools.getCalendar();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
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
            AiringModel.addSystemAiring(arr[0]);
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
            AiringModel.addSystemAiring(arr[0]);
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
            AiringModel.addSystemAiring(arr[0]);
            if(arr[4] == 1){
                Calendar cal = JkTools.getCalendar();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, 1);
                arr[1] = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        }
    }
}
