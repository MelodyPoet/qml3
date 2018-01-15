package base;

import comm.BaseVoJoin;
import protocol.MapEnteredPVo;

public   class MapEnteredPVoJoin extends BaseVoJoin<MapEnteredPVo> {
      public static MapEnteredPVoJoin instance = new MapEnteredPVoJoin();



    @Override
    public int fromSplitStr(MapEnteredPVo vo, String[] str, int time) {
        return 0;
    }

    @Override
    public int fromSplitStr(MapEnteredPVo vo, int[] str, int time) {
        vo.key=(short)str[time++];
        vo.value= (short) str[time++];
        vo.time = str[time++];
        return 3;
    }

    @Override
    public String toSplitStr(MapEnteredPVo vo) {
        return vo.key+","+vo.value+","+vo.time+",";
    }
}
