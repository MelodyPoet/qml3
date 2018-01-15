package redness;

import base.UserActState;
import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.RP_RoomGameReadyRqst;
import protocol.RP_RoomGameReadyRspd;
import protocol.RP_RoomGameStartRspd;
import protocol.ServerTipRspd;

/**
 * Created by admin on 2016/11/18.
 */
public class RP_RoomGameReadyCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        RP_RoomGameReadyRqst rqst=(RP_RoomGameReadyRqst) baseRqst;
        if (client.currentUser.rednessModel.myRoom == null) return;
        RP_RoomVo roomVo = client.currentUser.rednessModel.myRoom;
        if(roomVo.isInGame == 1){
            new ServerTipRspd(client,(short)320,null);
            return;
        }
        if(!user.rednessModel.isCanEnter(roomVo.mapID)){
            new ServerTipRspd(client,(short)223,null);
            return;
        }
        if(roomVo.ownner!=user.cacheUserVo&&roomVo.guest!=user.cacheUserVo)return;
        if(roomVo.ownner==user.cacheUserVo){
       if(roomVo.guest==null||roomVo.guestReady==false){
           new ServerTipRspd(client,(short)186,null);
           if(roomVo.guest!=null){
               new RP_RoomGameReadyRspd(client,roomVo.guestReady);
           }
           return;
       }
            if(roomVo.guest.isRobot()==false&&roomVo.guest.onlineUser!=null){
                if(!roomVo.guest.onlineUser.rednessModel.isCanEnter(roomVo.mapID)){
                    new ServerTipRspd(client,(short)224,null);
                    return;
                }
            }

            user.actState = UserActState.xshdFighing;
            new RP_RoomGameStartRspd(client);
            if(roomVo.guest.isRobot()==false&&roomVo.guest.onlineUser!=null) {
                roomVo.guest.onlineUser.actState = UserActState.xshdFighing;
                user.friendModel.addTeamedList(roomVo.guest.guid);
                roomVo.guest.onlineUser.friendModel.addTeamedList(user.guid);

                new RP_RoomGameStartRspd(roomVo.guest.onlineUser.client);
            }
        }else {
            roomVo.guestReady=rqst.ready;
            new RP_RoomGameReadyRspd(client,rqst.ready);
            if(roomVo.ownner.isRobot()==false&&roomVo.ownner.onlineUser!=null){
                new RP_RoomGameReadyRspd(roomVo.ownner.onlineUser.client, rqst.ready);
            }else{
                user.rednessModel.startGameTime = 2;//等待两秒
            }
//            new RP_RoomGameStartRspd(client);
  //          new RP_RoomGameStartRspd(roomVo.ownner.client);
        }

    }
}
