package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;

/**
 * Created by admin on 2016/7/6.
 */
public class GangInfoCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(user.cacheUserVo.gang.gangVo == null)return;
        user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
    }
}
