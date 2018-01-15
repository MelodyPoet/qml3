package redness;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.InviteMePVo;
import protocol.RP_InviteRqst;
import protocol.RP_InviteRspd;

/**
 * Created by admin on 2017/3/24.
 */
public class RP_InviteCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RP_InviteRqst rqst = (RP_InviteRqst) baseRqst;
        if(user.rednessModel.myRoom == null)return;
//        CachePassportVo passportVo = CachePassportVo.guidMap.get(rqst.tempID);
//        if(passportVo == null)return;
//        Client other = Client.getOne(passportVo.devID,passportVo.serverID);
//        if(other == null || other.currentUser == null)return;
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.tempID);
        if(cacheUserVo == null)return;
        InviteMePVo inviteMePVo = new InviteMePVo();
        inviteMePVo.roomID = user.rednessModel.myRoom.roomID;
        inviteMePVo.mapID = user.rednessModel.myRoom.mapID;
        inviteMePVo.name = client.currentUser.cacheUserVo.name;
        if(cacheUserVo.onlineUser == null)return;
        cacheUserVo.onlineUser.rednessModel.inviteMeList.add(inviteMePVo);
        new RP_InviteRspd(client, rqst.tempID,JkTools.getGameServerTime(client)+ Model.GameSetBaseMap.get(27).intArray[2]);
    }
}
