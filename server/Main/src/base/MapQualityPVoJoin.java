package base;

import comm.BaseVoJoin;
import protocol.MapQualityPVo;

import java.util.ArrayList;

public   class MapQualityPVoJoin extends BaseVoJoin<MapQualityPVo> {
      public static MapQualityPVoJoin instance = new MapQualityPVoJoin();



    @Override
    public int fromSplitStr(MapQualityPVo vo, String[] str, int start) {
        return 0;
    }

    @Override
    public int fromSplitStr(MapQualityPVo vo, int[] str, int start) {
        vo.ID=str[start++];
        int len = str[start++];
        for(int i=0;i<len;i++){
            vo.stars.add((byte) str[start++]);
        }
        return len+2;
    }

    @Override
    public String toSplitStr(MapQualityPVo vo) {
        StringBuffer sb = new StringBuffer();
        sb.append(vo.ID+","+vo.stars.size()+",");
        ArrayList<Byte> list = new ArrayList<>();
        list.addAll(vo.stars);
        for(int i=0;i<list.size();i++){
            sb.append(list.get(i)+",");
        }
        return sb.toString();
    }
}
