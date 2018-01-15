package illustrated;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.IllustratedRqst;
import protocol.IllustratedRspd;

/**
 * Created by admin on 2016/8/17.
 */
public class IllustratedCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        IllustratedRqst rqst = (IllustratedRqst) baseRqst;
        getIllustrated(client,user);
    }

    private void getIllustrated(Client client, User user){
        new IllustratedRspd(client,user.illustratedModel.monsterGroupList);
    }
}
