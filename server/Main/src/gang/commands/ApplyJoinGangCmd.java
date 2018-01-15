package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.GangOfficeEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2016/7/6.
 */
public class ApplyJoinGangCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ApplyJoinGangRqst rqst = (ApplyJoinGangRqst) baseRqst;
        applyJoinGang(client, user, rqst.gangID);
    }

    private void applyJoinGang(Client client, User user, long gangID) {
        if (user.cacheUserVo.gang.gangVo != null) {
            new ServerTipRspd(client, (short) 84, null);
            return;
        }
        int time = JkTools.getGameServerTime(null) - user.getUserData(UserDataEnum.NEXT_TIME_JOIN_GANG);
        if(time < 0){
            new ServerTipRspd(client,(short)137,null);
            return;
        }
        if (!Gang.allGangMap.containsKey(gangID)) return;
        GangVo gangVo = Gang.allGangMap.get(gangID);
        if(gangVo.users.size() >= Model.GameSetBaseMap.get(11).intArray[0]){
            new ServerTipRspd(client,(short)132,null);
            return;
        }
        if(gangVo.users.size() >= gangVo.maxUserCount){
            new ServerTipRspd(client,(short)133,null);
            return;
        }
        if (user.zdl < gangVo.zdlLimit) {
            if(gangVo.gangApplyMap.containsKey(user.guid)){
                new ServerTipRspd(client,(short)145,null);
                return;
            }
            GangApplyPVo pVo = new GangApplyPVo();
            pVo.userID = user.guid;
            pVo.name = user.cacheUserVo.name;
            pVo.level = (byte)user.getUserData(UserDataEnum.LEVEL);
            pVo.passportID = user.guid;
            pVo.portrait = user.cacheUserVo.portrait;
            pVo.zdl = user.zdl;
            pVo.time = JkTools.getGameServerTime(null);
            gangVo.gangApplyMap.put(user.guid,pVo);
            gangVo.gangApplyList.add(pVo);
            new ServerTipRspd(client,(short)146,null);
        }else{
            user.cacheUserVo.gang.join(gangVo, user.cacheUserVo, GangOfficeEnum.MEMBER,true);
            new ServerTipRspd(client,(short)317,gangVo.gangName);
        }
    }
}
