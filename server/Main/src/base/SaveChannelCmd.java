package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SaveChannelRqst;
import sqlCmd.AllSql;

/**
 * Created by admin on 2017/4/3.
 */
public class SaveChannelCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SaveChannelRqst rqst = (SaveChannelRqst)baseRqst;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_CHANNEL_STR,"'"+rqst.channelStr+"'");
    }
}
