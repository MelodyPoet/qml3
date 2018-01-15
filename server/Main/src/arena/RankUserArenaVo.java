package arena;

import comm.CacheUserVo;
import comm.RankListItem;
import protocol.ArenaRecordPVo;

import java.util.LinkedList;

/**
 * Created by jackie on 14-5-3.
 */
public class RankUserArenaVo extends RankListItem {
    public LinkedList<ArenaRecordPVo> arenaRecordList;
    public int lastDayIndex=-1;

    public RankUserArenaVo(CacheUserVo userVo) {
        cacheUserVo=userVo;
        userVo.rankArenaVo=this;
    }

    public void addArenaRecord(ArenaRecordPVo recordPVo) {
        if(arenaRecordList==null)arenaRecordList=new LinkedList<>();
        arenaRecordList.addFirst(recordPVo);
        if(arenaRecordList.size()>10)arenaRecordList.removeLast();
    }
    @Override
    public int orderScore() {
        return 0;
    }
}