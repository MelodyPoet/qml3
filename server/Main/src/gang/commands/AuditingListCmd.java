package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.AuditingListRspd;

/**
 * Created by admin on 2016/11/7.
 */
public class AuditingListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        getAuditingList(client,user);
    }

    private void getAuditingList(Client client,User user){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        new AuditingListRspd(client,gangVo.gangApplyList);
    }
}
