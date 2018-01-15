package arena;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ArenaGetMainRspd;


public class ArenaGetMainCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {



        if(user.cacheUserVo.rankArenaVo == null){
            user.cacheUserVo.rankArenaVo=new RankUserArenaVo(user.cacheUserVo);
            user.cacheUserVo.rankArenaVo.orderIndex = -2;
        }
        int dt=0;

            dt=user.arenaModel.nextFightTime- JkTools.getGameServerTime(client);
        if(dt<0)dt=0;
        new ArenaGetMainRspd(client,user.cacheUserVo.rankArenaVo.orderIndex,dt,user.cacheUserVo.rankArenaVo.lastDayIndex);
    }

	 

}
