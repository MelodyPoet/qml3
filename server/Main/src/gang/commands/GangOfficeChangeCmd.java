package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gang.RankGangUserVo;
import gluffy.comm.BaseRqst;
import protocol.GangOfficeChangeRqst;
import protocol.GangOfficeChangeRspd;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;
import table.GangOfficeEnum;

/**
 * Created by admin on 2016/10/31.
 */
public class GangOfficeChangeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangOfficeChangeRqst rqst = (GangOfficeChangeRqst)baseRqst;
        changeGangOffice(client,user,rqst.type,rqst.userID);
    }

    private void changeGangOffice(Client client,User user,byte type,long userID){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!user.cacheUserVo.gang.isMaster(user.guid))return;
        if(!gangVo.users.containsKey(userID))return;
        GangUserVo gangUserVo = gangVo.users.get(userID);
        switch (type){
            case GangOfficeEnum.MASTER:
                gangVo.master.office = GangOfficeEnum.MEMBER;
                AllSql.gangMemberSql.update(gangVo.master,AllSql.gangMemberSql.FIELD_OFFICE,GangOfficeEnum.MEMBER);
                RankGangUserVo rankGangUserVo = user.cacheUserVo.rankGangUserVo;
                int newScore = RankGangUserVo.calculate(rankGangUserVo.cacheUserVo.zdl,gangVo.master.office);
                gangVo.rankGangUserList.sortItem(rankGangUserVo,newScore);
                rankGangUserVo.office = gangVo.master.office;
                gangUserVo.office = GangOfficeEnum.MASTER;
                gangVo.master = gangUserVo;
                AllSql.gangMemberSql.update(gangVo.master,AllSql.gangMemberSql.FIELD_OFFICE,GangOfficeEnum.MASTER);
                gangVo.addLog((short) 1005,gangUserVo.cacheUserVo.name);
                break;
            case GangOfficeEnum.ELDER:
                int count = 0;
                for(GangUserVo userVo : gangVo.users.values()){
                    if(userVo.office == GangOfficeEnum.ELDER)count++;
                }
                if(count >= 10){
                    new ServerTipRspd(client,(short) 143,null);
                    return;
                }
                gangUserVo.office = GangOfficeEnum.ELDER;
                AllSql.gangMemberSql.update(gangUserVo,AllSql.gangMemberSql.FIELD_OFFICE,GangOfficeEnum.ELDER);
                gangVo.addLog((short) 1003,gangUserVo.cacheUserVo.name);
                break;
            case GangOfficeEnum.MEMBER:
                gangUserVo.office = GangOfficeEnum.MEMBER;
                AllSql.gangMemberSql.update(gangUserVo,AllSql.gangMemberSql.FIELD_OFFICE,GangOfficeEnum.MEMBER);
                gangVo.addLog((short) 1004,gangUserVo.cacheUserVo.name);
                break;
        }
        gangUserVo.cacheUserVo.gangStatus = (byte)(3 + type);
        AllSql.userSql.update(userID,AllSql.userSql.FIELD_GANG_STATUS,gangUserVo.cacheUserVo.gangStatus);
//        Client other = Client.allOnline.get(gangUserVo.cacheUserVo.passportVo.devID);
//        if (other != null && other.currentUser.guid == gangUserVo.cacheUserVo.guid) {
//            gangVo.createGangInfoRspd(other);
//            new GangOfficeChangeRspd(other,type,other.passportVo.guid,gangUserVo.cacheUserVo.guid);
//        }
        gangVo.createGangInfoRspd(client);
        new GangOfficeChangeRspd(client,type,gangUserVo.cacheUserVo.guid,gangUserVo.cacheUserVo.guid);
        RankGangUserVo rankGangUserVo = gangUserVo.cacheUserVo.rankGangUserVo;
        int newScore = RankGangUserVo.calculate(rankGangUserVo.cacheUserVo.zdl,type);
        gangVo.rankGangUserList.sortItem(rankGangUserVo,newScore);
        rankGangUserVo.office = type;
    }
}
