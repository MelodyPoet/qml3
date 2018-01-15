package rank;

import comm.CacheUserVo;
import comm.RankListItem;


/**
 * Created by jackie on 14-5-3.
 */
public class RankUserZdlVo extends RankListItem {

    public RankUserZdlVo(CacheUserVo userVo) {
        cacheUserVo=userVo;
        userVo.rankVo=this;
    }

    @Override
    public int orderScore() {
        return cacheUserVo.zdl;
    }

}