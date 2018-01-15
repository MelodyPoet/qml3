package mine;

import comm.CacheUserVo;
import comm.RankListItem;

/**
 * Created by admin on 2017/7/21.
 */
public class RankMineVo extends RankListItem {
    public int mineCount;
    public RankMineVo(CacheUserVo userVo) {
        cacheUserVo=userVo;
        userVo.rankMineVo=this;
    }
    @Override
    public int orderScore() {
        return mineCount;
    }
}
