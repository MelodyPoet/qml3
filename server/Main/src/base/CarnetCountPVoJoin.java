package base;

import comm.BaseVoJoin;
import protocol.IntIntPVo;

/**
 * Created by admin on 2017/2/14.
 */
public class CarnetCountPVoJoin extends BaseVoJoin<IntIntPVo>{
    public static CarnetCountPVoJoin instance = new CarnetCountPVoJoin();

    @Override
    public int fromSplitStr(IntIntPVo vo, String[] str, int start) {
        return 0;
    }

    @Override
    public int fromSplitStr(IntIntPVo vo, int[] str, int start) {
        vo.key = str[start++];
        vo.value = str[start++];
        return 2;
    }

    @Override
    public String toSplitStr(IntIntPVo vo) {
        return vo.key+","+vo.value+",";
    }
}
