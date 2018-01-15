package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangLogRqst;
import protocol.GangLogRspd;

/**
 * Created by admin on 2016/11/8.
 */
public class GangLogCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangLogRqst rqst =(GangLogRqst)baseRqst;
        getGangLog(client,user);
    }

    private void getGangLog(Client client,User user){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        new GangLogRspd(client,gangVo.gangLogList);
    }
}
