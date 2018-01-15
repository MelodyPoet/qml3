package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GangBuildInfoRqst;

/**
 * Created by admin on 2016/10/21.
 */
public class GangBuildInfoCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangBuildInfoRqst rqst = (GangBuildInfoRqst)baseRqst;
        getGangBuildInfo(client,user);
    }

    private void getGangBuildInfo(Client client,User user){
        if(user.cacheUserVo.gang.gangVo == null)return;
        user.cacheUserVo.gang.gangVo.createGangBuildInfoRspd(client);
    }
}
