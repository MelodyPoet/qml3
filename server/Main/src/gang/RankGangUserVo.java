package gang;


import comm.RankListItem;
import table.GangOfficeEnum;

/**
 * Created by admin on 2016/11/14.
 */
public class RankGangUserVo extends RankListItem {
    public byte office;
    public RankGangUserVo(GangUserVo gangUserVo) {
        cacheUserVo=gangUserVo.cacheUserVo;
        office = gangUserVo.office;
        cacheUserVo.rankGangUserVo = this;
    }

    @Override
    public int orderScore() {
        return calculate(cacheUserVo.zdl,office);
    }
    public static int calculate(int zdl,byte office){
        if(office == GangOfficeEnum.MASTER){
            return zdl + 1500000000;
        }else if(office == GangOfficeEnum.ELDER){
            return zdl + 1000000000;
        }else{
            return zdl;
        }
    }
}
