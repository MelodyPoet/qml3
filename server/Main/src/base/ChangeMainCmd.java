package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ChangeMainRspd;
import protocol.GoMapRqst;

/**
 * Created by admin on 2017/4/14.
 */
public class ChangeMainCmd extends BaseRqstCmd{
    public static boolean flag = false;
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GoMapRqst rqst = new GoMapRqst();
        if(flag){
            rqst.mapID = 1;
            flag = false;
        }else{
            rqst.mapID = 3;
            flag = true;
        }
        new GoMapCmd().execute(client,user,rqst);
        new ChangeMainRspd(client, JkTools.getGameServerTime(client)+300);
    }
}
