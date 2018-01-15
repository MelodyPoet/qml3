package redness;

import comm.CacheUserVo;
import comm.RankListItem;

/**
 * Created by admin on 2017/3/30.
 */
public class RankRednessVo extends RankListItem {
    public int weekRednessMoney;

    public RankRednessVo(CacheUserVo userVo) {
        cacheUserVo=userVo;
        userVo.rankRednessVo=this;
    }

    @Override
    public int orderScore() {
        return weekRednessMoney;
    }
}
