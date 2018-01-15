package gameset;

import comm.Model;
import gang.Gang;
import gang.GangVo;
import gang.guess.GuessModel;
import gluffy.utils.JkTools;
import table.SystemUpdateBaseVo;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by admin on 2017/6/9.
 */
public class GameSetModel {
    public static GameSetVo gameSetVo;
    public static HashMap<Integer, ActiveVo> timeMap = new HashMap<>();

    public static boolean checkTime(int id){
        if(!timeMap.containsKey(id))return updateTime(id);
        ActiveVo vo = timeMap.get(id);
        int currentTime = JkTools.getGameServerTime(null);
        if(currentTime < vo.startTime)return false;
        if(currentTime > vo.endTime)return updateTime(id);
        if(!vo.isOpen){
            vo.isOpen = true;
            open(id);
            gameSetVo.saveActive();
        }
        return true;
    }

    private static boolean updateTime(int id){
        int currentTime = JkTools.getGameServerTime(null);
        int startTime = getTime(id);
        int endTime = getRelevancyTime(id);
        if (endTime < startTime)
        {
            if (currentTime < endTime){
                startTime = startTime - Model.ONE_WEEK_TIME;
            }else{
                endTime = endTime + Model.ONE_WEEK_TIME;
            }
        }
        ActiveVo activeVo = null;
        if(timeMap.containsKey(id)){
            activeVo = timeMap.get(id);
            if(activeVo.isOpen){
                close(id);
            }
        }else{
            activeVo = new ActiveVo();
            timeMap.put(id,activeVo);
        }
        activeVo.endTime = endTime;
        activeVo.startTime = startTime;
        activeVo.isOpen = currentTime >= startTime && currentTime < endTime;
        if(activeVo.isOpen){
            open(id);
        }
        System.out.println("startTime ======"+startTime+"endTime=========="+endTime+"currentTime====="+currentTime);
        gameSetVo.saveActive();
        return activeVo.isOpen;
    }

    private static void open(int id){
        switch (id){
            case 1:
                break;
            case 3:
                GuessModel.openAll();
                break;
            case 5:
                break;
        }
    }

    private static void close(int id){
        switch (id){
            case 1:
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
                break;
            case 5:
                break;
        }
    }

    private static int getTime(int id){
        Calendar calendar = JkTools.getCalendar();
        SystemUpdateBaseVo vo = Model.SystemUpdateBaseMap.get(id);
        int hour = vo.minute/60;
        int minute = vo.minute%60;
        calendar.add(Calendar.HOUR_OF_DAY,hour - calendar.get(Calendar.HOUR_OF_DAY));
        calendar.add(Calendar.MINUTE,minute - calendar.get(Calendar.MINUTE));
        calendar.add(Calendar.SECOND,0 - calendar.get(Calendar.SECOND));
        if(vo.type == 2){
            int weekday = calendar.get(Calendar.DAY_OF_WEEK);
            weekday =(weekday-1)%7;
            if(weekday == 0){
                weekday = 7;
            }
            calendar.add(Calendar.DAY_OF_YEAR,vo.typeAtrribute - weekday);
        }
        return JkTools.getGameServerTime(calendar.getTimeInMillis());
    }

    private static int getRelevancyTime(int id){
        int relevancyID = Model.SystemUpdateBaseMap.get(id).relevancyID;
        return getTime(relevancyID);
    }
}
