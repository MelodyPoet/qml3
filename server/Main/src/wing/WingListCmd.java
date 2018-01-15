package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingListRspd;

/**
 * Created by admin on 2017/4/27.
 */
public class WingListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        new WingListRspd(client, user.cacheUserVo.wingModel.wingSet);
    }
}
