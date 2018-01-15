package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.UserUpdateRqst;

/**
 * Created by admin on 2017/4/15.
 */
public class UserUpdateCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        UserUpdateRqst rqst = (UserUpdateRqst) baseRqst;
        user.userUpdateModel.update(rqst.minute, true);
    }
}
