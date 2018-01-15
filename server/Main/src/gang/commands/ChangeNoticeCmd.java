package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ChangeNoticeRqst;
import sqlCmd.AllSql;

/**
 * Created by admin on 2016/7/12.
 */
public class ChangeNoticeCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ChangeNoticeRqst rqst = (ChangeNoticeRqst) baseRqst;
        changeNotice(client, user, rqst.newNotice);
    }

    private void changeNotice(Client client, User user, String newNotice) {
        if (user.cacheUserVo.gang.gangVo == null) return;
        if (user.cacheUserVo.gang.isMember(user.guid)) return;
        if (!checkNotice(newNotice)) return;
        AllSql.gangSql.update(user.cacheUserVo.gang.gangVo, AllSql.gangSql.FIELD_NOTICE, "\'" + newNotice + "\'");
        user.cacheUserVo.gang.gangVo.notice = newNotice;
        user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
    }

    private boolean checkNotice(String newNotice) {
        if (newNotice.length() >= 0 && newNotice.length() <= 40) {
            return true;
        }
        return false;
    }
}
