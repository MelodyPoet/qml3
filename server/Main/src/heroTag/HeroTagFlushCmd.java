package heroTag;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;

/**
 * Created by admin on 2017/8/4.
 */
public class HeroTagFlushCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        user.heroTagModel.flushHeroTag();
        user.updateZDL();
    }
}
