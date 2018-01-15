package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ContributeRqst;
import protocol.ContributeRspd;
import sqlCmd.AllSql;
import table.GangContributeBaseVo;
import table.GangUserDataEnum;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;

/**
 * Created by admin on 2016/10/20.
 */
public class ContributeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ContributeRqst rqst = (ContributeRqst) baseRqst;
        contribute(client,user,rqst.type);
    }

    private void contribute(Client client,User user,byte type){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo userVo = gangVo.users.get(user.guid);
        int time = JkTools.getGameServerTime(client) - userVo.endCDTime;
        int limit = Model.GameSetBaseMap.get(8).intArray[0];
        int seconds = Model.GameSetBaseMap.get(8).intArray[1];
        switch (type){
            case 0:
                if(userVo.endCDTime == -1)break;
                if(time >= 0){
                    userVo.contributeTime = (byte) Math.min(limit,time/seconds+1);
                    AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_CONTRIBUTE_TIME,userVo.contributeTime);
                    if(userVo.contributeTime == limit){
                        userVo.endCDTime = -1;
                    }else{
                        userVo.endCDTime = JkTools.getGameServerTime(client) + seconds - time%seconds;
                    }
                    AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_END_CD_TIME,userVo.endCDTime);
                }
                break;
            case 1:
            case 2:
            case 3:
                GangContributeBaseVo vo = Model.GangContributeBaseMap.get((int)type);
                if(vo == null)break;
                if(user.costUserDataAndPropList(vo.costData,true, ReasonTypeEnum.CONTRIBUTE,null) == false)break;
                boolean contribute = contributeCheck(client,userVo,time,seconds,limit);
                if(contribute){
                    gangVo.addExp(client,vo.effect[0]);
                    userVo.addGangUserData(GangUserDataEnum.CONTRIBUTION,vo.effect[1],true);
                }
                user.activationModel.progressBuyAct(MissionConditionEnum.GANG_CONTRIBUTE,0);
                break;
        }
        new ContributeRspd(client,userVo.contributeTime,userVo.endCDTime);
    }

    private boolean contributeCheck(Client client,GangUserVo userVo,int seconds,int hours,int limit){
        if(seconds < 0){
            if(userVo.contributeTime <= 0)return false;
            userVo.contributeTime--;
            AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_CONTRIBUTE_TIME,userVo.contributeTime);
        }else{
            if(userVo.endCDTime == -1){
                userVo.contributeTime--;
                userVo.endCDTime = JkTools.getGameServerTime(client) + hours;
            }else{
                userVo.contributeTime = (byte) Math.min(limit-1,userVo.contributeTime+seconds/hours);
                if(userVo.contributeTime == limit-1){
                    userVo.endCDTime = JkTools.getGameServerTime(client) + hours;
                }else{
                    userVo.endCDTime = JkTools.getGameServerTime(client) + hours - seconds%hours;
                }
            }
            AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_CONTRIBUTE_TIME,userVo.contributeTime);
            AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_END_CD_TIME,userVo.endCDTime);
        }
        return true;
    }
}
