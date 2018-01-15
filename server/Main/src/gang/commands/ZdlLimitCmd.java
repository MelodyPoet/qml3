package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.ZdlLimitRqst;
import sqlCmd.AllSql;

/**
 * Created by admin on 2016/11/7.
 */
public class ZdlLimitCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ZdlLimitRqst rqst = (ZdlLimitRqst)baseRqst;
        limitZDL(client,user,rqst.limit);
    }

    private void limitZDL(Client client,User user,int limit){
        if(limit < -1)return;
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        gangVo.zdlLimit = limit;
        AllSql.gangSql.update(gangVo,AllSql.gangSql.FIELD_ZDL_LIMIT,limit);
        gangVo.createGangInfoRspd(client);
        new GeneralSuccessRspd(client,ZdlLimitRqst.PRO_ID);
    }
}
