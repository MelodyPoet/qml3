package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.KickOutGangRqst;
import sqlCmd.AllSql;
import table.RankEnum;

/**
 * Created by admin on 2016/7/6.
 */
public class KickOutGangCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        KickOutGangRqst rqst = (KickOutGangRqst) baseRqst;
        kickOutGang(client, user, rqst.userID);
    }

    private void kickOutGang(Client client, User user, Long userID) {
        if (user.cacheUserVo.gang.isMember(user.guid)) return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo.master.cacheUserVo.guid == userID)return;
        if (!gangVo.users.containsKey(userID)) return;
        GangUserVo gangUserVo = gangVo.users.get(userID);
        gangVo.zdl -= gangUserVo.cacheUserVo.zdl;
        AllSql.gangSql.update(gangVo, AllSql.gangSql.FIELD_ZDL, gangVo.zdl);
//        Client other = Client.allOnline.get(gangUserVo.cacheUserVo.passportVo.devID);
        gangVo.users.remove(userID);
        gangUserVo.cacheUserVo.gang.gangVo = null;
        AllSql.gangMemberSql.delete(gangUserVo.guid);
        AllSql.userSql.update(gangUserVo.cacheUserVo.guid,AllSql.userSql.FIELD_GANG_SKILL,null);
        gangUserVo.cacheUserVo.gangStatus = 2;
        AllSql.userSql.update(userID,AllSql.userSql.FIELD_GANG_STATUS,2);
        gangUserVo.cacheUserVo.gangName = gangVo.gangName;
//        if (other != null && other.currentUser.guid == gangUserVo.cacheUserVo.guid) {
//            IsJoinGangCmd.isJoinGang(other, other.currentUser, (byte) 2);
//            other.currentUser.gangSkillModel.clearGangSkill(false,gangVo.gangName);
//        }
        new GeneralSuccessRspd(client,KickOutGangRqst.PRO_ID);
        gangVo.addLog((short) 1006,gangUserVo.cacheUserVo.name);
        gangVo.rankGangUserList.remove(gangUserVo.cacheUserVo.rankGangUserVo, RankEnum.GangUSER);
    }
}
