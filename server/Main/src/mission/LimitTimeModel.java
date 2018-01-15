package mission;

import comm.Model;
import table.LimitTimeActivationBaseVo;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/8.
 */
public class LimitTimeModel {
    public static ArrayList<LimitTimeActivationBaseVo> creartList;
    public static ArrayList<LimitTimeActivationBaseVo> levelList;
    public static ArrayList<LimitTimeActivationBaseVo> timeList;
    public static void init(){
        creartList = new ArrayList<>();
        levelList = new ArrayList<>();
        timeList = new ArrayList<>();
        for(LimitTimeActivationBaseVo vo : Model.LimitTimeActivationBaseMap.values()){
            switch (vo.triggerType){
                case 0:
                    timeList.add(vo);
                    break;
                case 1:
                    creartList.add(vo);
                    break;
                case 2:
                    levelList.add(vo);
                    break;
            }
        }
    }
}
