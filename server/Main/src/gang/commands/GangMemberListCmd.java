package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangMemberListRqst;

/**
 * Created by admin on 2016/10/11.
 */
public class GangMemberListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangMemberListRqst rqst = (GangMemberListRqst)baseRqst;
        if(!Gang.allGangMap.containsKey(rqst.gangID))return;
        GangVo gangVo = Gang.allGangMap.get(rqst.gangID);
        if(gangVo == null)return;
        gangVo.createGangMemberRspd(client);
    }
}
