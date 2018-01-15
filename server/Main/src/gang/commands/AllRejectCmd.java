package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.AllRejectRqst;
import protocol.GeneralSuccessRspd;

/**
 * Created by admin on 2016/11/7.
 */
public class AllRejectCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        AllRejectRqst rqst = (AllRejectRqst)baseRqst;
        rejectAll(client,user);
    }

    private void rejectAll(Client client,User user){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(user.cacheUserVo.gang.isMember(user.guid))return;
        gangVo.gangApplyMap.clear();
        gangVo.gangApplyList.clear();
        gangVo.saveGangApplyInfo();
        new GeneralSuccessRspd(client, AllRejectRqst.PRO_ID);
        gangVo.addLog((short) 1008,user.cacheUserVo.name);
    }
}
