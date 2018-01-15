package mail;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.MailListRqst;

/**
 * Created by admin on 2016/7/27.
 */
public class MailListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MailListRqst rqst = (MailListRqst) baseRqst;
        getMailList(client,user);
    }

    private void getMailList(Client client, User user){
        user.mailModel.createMailListRspd();
    }
}
