package arena;

import rank.RankModel;
import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ArenaGetTargetRspd;
import protocol.RankUserPVo;
import table.RankEnum;

import java.util.ArrayList;


public class ArenaGetTargetCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        int orderIndex = 0;
        if(user.cacheUserVo.rankArenaVo.orderIndex == -2){
            orderIndex = RankModel.rankArenaList.size();
        }else{
            orderIndex = user.cacheUserVo.rankArenaVo.orderIndex;
        }

        ArrayList<RankUserPVo> targets=new ArrayList<>();
        if(RankModel.rankArenaList.size()<7){
            for (RankUserArenaVo arenaVo : RankModel.rankArenaList.list){
                if(arenaVo.cacheUserVo.guid!=user.guid) {
                    targets.add(arenaVo.makeRankPvo(RankEnum.ARENA));
                }
            }
            new ArenaGetTargetRspd(client, targets);
            return;
        }
        ArrayList<Integer> targetRanks= ArenaModel.getTargets(orderIndex+1);
        for (Integer i:targetRanks){
            i--;
            if(RankModel.rankArenaList.size()<=i)continue;
            targets.add(RankModel.rankArenaList.get(i).makeRankPvo(RankEnum.ARENA));
        }
        new ArenaGetTargetRspd(client, targets);
    }

	 

}
