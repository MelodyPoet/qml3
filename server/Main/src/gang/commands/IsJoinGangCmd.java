package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.IsJoinGangRqst;
import protocol.IsJoinGangRspd;

/**
 * Created by admin on 2016/7/7.
 */
public class IsJoinGangCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        IsJoinGangRqst rqst = (IsJoinGangRqst) baseRqst;
        isJoinGang(client,user,(byte) 0);
    }

    public static void isJoinGang(Client client, User user,byte type){
        long gangID = 0;
        if(user.cacheUserVo.gang.gangVo != null){
            gangID = user.cacheUserVo.gang.gangVo.gangID;
        }
        new IsJoinGangRspd(client,type,gangID);
        if(gangID != 0){
            user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
        }
    }
}
