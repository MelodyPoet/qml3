package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangUserVo;
import gang.GangVo;
import gang.RankGangUserVo;
import gluffy.comm.BaseRqst;
import protocol.QuitGangRqst;
import sqlCmd.AllSql;
import table.GangOfficeEnum;
import table.RankEnum;

/**
 * Created by admin on 2016/7/6.
 */
public class QuitGangCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        QuitGangRqst rqst = (QuitGangRqst) baseRqst;
        quitGang(client,user);
    }

    private void quitGang(Client client,User user){
        if(user.cacheUserVo.gang.gangVo != null){
            GangVo gangVo = user.cacheUserVo.gang.gangVo;
            if(gangVo.master.cacheUserVo.guid == user.guid){
//                new ServerTipRspd(client,(short)136,null);
                if(gangVo.users.size()<=1){
                    AllSql.gangSql.delete(gangVo.gangID);
                    Gang.allGangList.remove(gangVo);
                    Gang.allGangMap.remove(gangVo.gangID);
                    Gang.usedName.remove(gangVo.gangName);
                    user.cacheUserVo.gang.gangVo = null;
                    AllSql.gangMemberSql.delete(gangVo.master.guid);
                    IsJoinGangCmd.isJoinGang(client,user,(byte) 1);
                    user.gangSkillModel.clearGangSkill(true,"");
                    gangVo.rankGangUserList.remove(user.cacheUserVo.rankGangUserVo,RankEnum.GangUSER);
                    return;
                }else{
                    GangUserVo newMaster = Gang.nextMaster(gangVo,user.guid);
                    newMaster.office = GangOfficeEnum.MASTER;
                    gangVo.master = newMaster;
                    AllSql.gangMemberSql.update(newMaster,AllSql.gangMemberSql.FIELD_OFFICE,GangOfficeEnum.MASTER);
                    gangVo.addLog((short) 1005,newMaster.cacheUserVo.name);
                    newMaster.cacheUserVo.gangStatus = 6;
                    AllSql.userSql.update(newMaster.cacheUserVo.guid,AllSql.userSql.FIELD_GANG_STATUS,6);
                    RankGangUserVo rankGangUserVo = newMaster.cacheUserVo.rankGangUserVo;
                    int newScore = RankGangUserVo.calculate(newMaster.cacheUserVo.zdl,newMaster.office);
                    gangVo.rankGangUserList.sortItem(rankGangUserVo,newScore);
                    rankGangUserVo.office = newMaster.office;
                }
            }
            gangVo.zdl -= user.zdl;
            AllSql.gangSql.update(gangVo,AllSql.gangSql.FIELD_ZDL,gangVo.zdl);
            if(gangVo.users.containsKey(user.guid)){
                GangUserVo gangUserVo = gangVo.users.get(user.guid);
                gangVo.users.remove(user.guid);
                AllSql.gangMemberSql.delete(gangUserVo.guid);
            }
            user.cacheUserVo.gang.gangVo = null;
            IsJoinGangCmd.isJoinGang(client,user,(byte) 1);
            gangVo.addLog((short) 1002,user.cacheUserVo.name);
            user.gangSkillModel.clearGangSkill(true,"");
            user.cacheUserVo.gang.addTalkList.clear();
            gangVo.rankGangUserList.remove(user.cacheUserVo.rankGangUserVo, RankEnum.GangUSER);
        }
    }
}
