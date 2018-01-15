package test.utilmode;


import comm.RankListItem;

/**
 * Created by admin on 2017/8/9.
 */
public class RankVo extends RankListItem {
    public int value;
    @Override
    public int orderScore() {
        return value;
    }
}
