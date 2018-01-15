package recharge;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.RechargeRqst;
import protocol.RechargeRspd;

/**
 * Created by admin on 2017/3/16.
 */
public class RechargeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RechargeRqst rqst = (RechargeRqst)baseRqst;
        new RechargeRspd(client,rqst.id);
    }
}
