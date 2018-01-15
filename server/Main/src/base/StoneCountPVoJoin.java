package base;

import comm.BaseVoJoin;
import protocol.ByteIntPVo;

/**
 * Created by admin on 2017/2/14.
 */
public class StoneCountPVoJoin extends BaseVoJoin<ByteIntPVo>{
    public static StoneCountPVoJoin instance = new StoneCountPVoJoin();

    @Override
    public int fromSplitStr(ByteIntPVo vo, String[] str, int start) {
        return 0;
    }

    @Override
    public int fromSplitStr(ByteIntPVo vo, int[] str, int start) {
        vo.key = (byte)str[start++];
        vo.value = str[start++];
        return 2;
    }

    @Override
    public String toSplitStr(ByteIntPVo vo) {
        return vo.key+","+vo.value+",";
    }
}
