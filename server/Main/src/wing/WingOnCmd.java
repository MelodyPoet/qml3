package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingOnRqst;
import protocol.WingOnRspd;

/**
 * Created by admin on 2017/4/27.
 */
public class WingOnCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WingOnRqst rqst= (WingOnRqst) baseRqst;
        WingModel wingModel = user.cacheUserVo.wingModel;
        if(rqst.id != 0 &&!wingModel.wingSet.contains(rqst.id))return;
        wingModel.equipWingID=rqst.id;
        wingModel.saveSqlData();
        new WingOnRspd(client,rqst.id);
    }
}
