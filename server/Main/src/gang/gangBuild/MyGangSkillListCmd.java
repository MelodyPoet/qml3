package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;

/**
 * Created by admin on 2017/5/17.
 */
public class MyGangSkillListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        user.gangSkillModel.checkUp();
    }
}
