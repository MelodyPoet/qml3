package comm;

import gluffy.utils.AbsRankListItem;
import protocol.MineRankPVo;
import protocol.RankUserPVo;
import table.RankEnum;
import table.TowerBaseVo;
import utils.UserVoAdapter;

public class RankListItem extends AbsRankListItem {

    @Override
    public int orderScore() {
        return 0;
    }

    @Override
    public void onRemove(byte type) {
        switch (type){
            case RankEnum.GangUSER:
               cacheUserVo.rankGangUserVo = null;
                break;
            case RankEnum.LAST_MINE:
                 cacheUserVo.rankMineVo = null;
                break;
        }
    }
        public CacheUserVo cacheUserVo;

    public RankUserPVo makeRankPvo(byte type){
        RankUserPVo ruVo= UserVoAdapter.toRankUserPVo(cacheUserVo);
        ruVo.rankScore=orderScore();
        ruVo.rankIndex=orderIndex;
        if(type == RankEnum.DUNGEON){
            ruVo.extraData=cacheUserVo.lastMap;
        }
        if(type == RankEnum.WORMNEST){
            TowerBaseVo vo = Model.TowerBaseMap.get(1).get(cacheUserVo.wormNestMaxFloor-1);
            if(vo != null){
                ruVo.extraData=vo.mapID;
            }
        }
        if(type == RankEnum.GangUSER){

        }
        return ruVo;
    }

    public MineRankPVo makeMineRankPVo(){
        MineRankPVo mineRankPVo= UserVoAdapter.toMineRankPVo(cacheUserVo);
        mineRankPVo.guid = cacheUserVo.guid;
        mineRankPVo.rankScore=orderScore();
        mineRankPVo.rankIndex=orderIndex;
        return mineRankPVo;
    }
}
