package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SaveYunwaIDRqst;
import sqlCmd.AllSql;

/**
 * Created by admin on 2017/4/7.
 */
public class SaveYunwaIDCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SaveYunwaIDRqst rqst = (SaveYunwaIDRqst)baseRqst;
        user.cacheUserVo.yunwaID = rqst.yunwaID;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_YUNWA_ID,rqst.yunwaID);
    }
}
