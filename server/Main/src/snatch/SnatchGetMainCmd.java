package snatch;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import table.UserDataEnum;


public class SnatchGetMainCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {

if(user.getUserData(UserDataEnum.LEVEL)<SnatchModel.needLvl)return;

       // int     dt=user.arenaModel.nextFightTime- JkTools.getGameServerTime();
       // if(dt<0)dt=0;
      //
      //  new ArenaGetMainRspd(client,user.cacheUserVo.rankArenaVo.orderIndex,dt);
      //  new SnatchGetMainRspd(client,dt,5);

    }

	 

}
