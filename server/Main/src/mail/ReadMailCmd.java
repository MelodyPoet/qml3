package mail;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ReadMailRqst;

/**
 * Created by admin on 2016/7/27.
 */
public class ReadMailCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ReadMailRqst rqst = (ReadMailRqst) baseRqst;
        MailModel.readMail(user,rqst.mailID,true);
    }
}
