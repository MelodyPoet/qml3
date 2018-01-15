package redness;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.RP_RoomEnterRspd;
import protocol.RP_RoomExitRqst;
import protocol.RP_RoomExitRspd;
import protocol.RP_RoomGameReadyRspd;

/**
 * Created by admin on 2016/11/18.
 */
public class RP_RoomExitCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        RP_RoomExitRqst rqst = (RP_RoomExitRqst)baseRqst;
        if (user.rednessModel.myRoom == null) return;
        RP_RoomVo roomVo = user.rednessModel.myRoom;
        CacheUserVo exitUser = user.cacheUserVo;
        if (roomVo.guest == user.cacheUserVo) {
            roomVo.guest = null;
        }else if(roomVo.ownner == user.cacheUserVo) {
            if(rqst.isMe){
                if(roomVo.guest!=null) {
                    roomVo.ownner = roomVo.guest;
                    roomVo.guest = null;
                }else{
                    roomVo.ownner=null;
                }
            }else{
                exitUser = roomVo.guest;
                roomVo.guest = null;
            }
        }
        if(roomVo.ownner != null){
            roomVo.guestReady = false;
            roomVo.needRobotTime = JkTools.getGameServerTime(client)+RednessModel.NEED_ROBOT_TIME;
            if(roomVo.ownner.isRobot()==false&&roomVo.ownner.onlineUser!=null){
                new RP_RoomEnterRspd(roomVo.ownner.onlineUser.client,roomVo.roomID,roomVo.mapID,roomVo.ownner.guid,roomVo.pwd, roomVo.getUsers());
                new RP_RoomGameReadyRspd(client,roomVo.guestReady);
            }
        }else{
            RednessModel.allRooms.remove(roomVo.roomID);
        }
        if(exitUser !=null && exitUser.isRobot()==false && exitUser.onlineUser != null){
            exitUser.onlineUser.rednessModel.myRoom = null;
            new RP_RoomExitRspd(exitUser.onlineUser.client,exitUser.guid);
        }
//        if(roomVo.ownner.isRobot()==false&&roomVo.ownner.onlineUser!=null)
//         new RP_RoomExitRspd(roomVo.ownner.onlineUser.client, client.passportVo.guid);
    }
}
