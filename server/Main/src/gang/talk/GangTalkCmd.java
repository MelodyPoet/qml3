package gang.talk;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GangTalkRqst;

/**
 * Created by admin on 2016/7/12.
 */
public class GangTalkCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangTalkRqst rqst = (GangTalkRqst) baseRqst;
        gangTalk(client,user,rqst.type,rqst.msg);
    }

    private void gangTalk(Client client, User user,byte type,String msg){
        if(user.cacheUserVo.gang.gangVo == null || user.cacheUserVo.gang.gangVo.gangTalkModel == null)return;
        user.cacheUserVo.gang.gangVo.gangTalkModel.addTalkMsgInTimeList(user,type,msg);
    }
}
