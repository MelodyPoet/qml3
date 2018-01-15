package rank;

import comm.CacheUserVo;
import comm.RankListItem;


/**
 * Created by jackie on 14-5-3.
 */
public class RankUserFbVo extends RankListItem {


    public RankUserFbVo(CacheUserVo userVo) {
        cacheUserVo=userVo;
        userVo.rankFbVo=this;
    }

    @Override
    public int orderScore() {
        return cacheUserVo.fbScore;
    }
}