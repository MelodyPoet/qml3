package gang.talk;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GetGangTalkListRqst;
import protocol.GetGangTalkListRspd;

/**
 * Created by admin on 2016/7/12.
 */
public class GetGangTalkListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetGangTalkListRqst rqst = (GetGangTalkListRqst) baseRqst;
        getGangTalkList(client,user);
    }

    private void getGangTalkList(Client client, User user){
        if(user.cacheUserVo.gang.gangVo == null || user.cacheUserVo.gang.gangVo.gangTalkModel == null)return;
        new GetGangTalkListRspd(client,user.cacheUserVo.gang.gangVo.gangTalkModel.talkList);
    }
}
