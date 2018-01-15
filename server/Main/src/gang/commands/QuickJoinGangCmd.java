package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.GeneralSuccessRspd;
import protocol.QuickJoinGangRqst;
import protocol.ServerTipRspd;
import table.GangOfficeEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2016/10/14.
 */
public class QuickJoinGangCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        QuickJoinGangRqst rqst = (QuickJoinGangRqst)baseRqst;
        quickJoinGang(client,user);
    }

    private void quickJoinGang(Client client,User user){
        if(user.cacheUserVo.gang.gangVo != null){
            new ServerTipRspd(client,(short)84,null);
            return;
        }
        int time = JkTools.getGameServerTime(null) - user.getUserData(UserDataEnum.NEXT_TIME_JOIN_GANG);
        if(time < 0){
            new ServerTipRspd(client,(short)137,null);
            return;
        }
        for(GangVo gangVo : Gang.allGangList){
            if(gangVo.users.size() >= gangVo.maxUserCount)continue;
            if(user.zdl < gangVo.zdlLimit)continue;
            user.cacheUserVo.gang.join(gangVo,user.cacheUserVo, GangOfficeEnum.MEMBER,true);
            user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
            new GeneralSuccessRspd(client,QuickJoinGangRqst.PRO_ID);
            break;
        }
        if(user.cacheUserVo.gang.gangVo == null){
            new ServerTipRspd(client,(short)126,null);
        }
    }
}
