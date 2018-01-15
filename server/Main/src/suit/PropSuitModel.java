package suit;

import comm.Model;
import table.PropSuitBaseVo;

/**
 * Created by admin on 2017/7/1.
 */
public class PropSuitModel {
    public static int[] getSuitAddAttr(int suit,int count){
        PropSuitBaseVo suitBaseVo = Model.PropSuitBaseMap.get(suit);
        if(suitBaseVo == null)return null;
        switch (count){
            case 2:
                return suitBaseVo.addAttr2;
            case 3:
                return suitBaseVo.addAttr3;
            case 4:
                return suitBaseVo.addAttr4;
        }
        return null;
    }
}
