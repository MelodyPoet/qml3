package arena;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ArenaRecordRspd;


public class ArenaRecordCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        if(user.cacheUserVo.rankArenaVo==null)return;
            if(user.cacheUserVo.rankArenaVo.arenaRecordList==null)return;
        new ArenaRecordRspd(client,user.cacheUserVo.rankArenaVo.arenaRecordList);

    }

	 

}
