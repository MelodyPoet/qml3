package skin;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SkinListRspd;

/**
 * Created by admin on 2016/8/26.
 */
public class SkinListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        new SkinListRspd(client,user.cacheUserVo.skinModel.skinMap.values());
    }
}
