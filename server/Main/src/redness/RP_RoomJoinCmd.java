package redness;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.MapBaseVo;

/**
 * Created by admin on 2016/11/18.
 */
public class RP_RoomJoinCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RP_RoomJoinRqst rqst = (RP_RoomJoinRqst)baseRqst;

        if(client.currentUser.rednessModel.myRoom!=null){
            RP_RoomExitRqst rp_roomExitRqst = new RP_RoomExitRqst();
            rp_roomExitRqst.isMe = true;
            new RP_RoomExitCmd().execute(client,user,rp_roomExitRqst);
        }
        RP_RoomVo roomVo = RednessModel.allRooms.get(rqst.roomID);
        if(roomVo == null){//房间已销毁
            new ServerTipRspd(client,(short)222,null);
            return;
        }
        if(roomVo.guest != null){//房间已满
            new ServerTipRspd(client,(short)221,null);
            return;
        }

        MapBaseVo mapBaseVo = Model.MapBaseMap.get(roomVo.mapID);
        if(mapBaseVo.needLevel > user.cacheUserVo.level){
            new ServerTipRspd(client,(short)24,null);
            return;
        }
        if(rqst.needVerity && !roomVo.pwd.equals(rqst.pwd)){
            new ServerTipRspd(client,(short)230,null);
            return;
        }

        if(!user.rednessModel.isCanEnter(roomVo.mapID)){
            new ServerTipRspd(client,(short)223,null);
            return;
        }
        if(roomVo.isInGame == 1){
            new ServerTipRspd(client,(short)310,null);
            return;
        }
        roomVo.needRobotTime = -1;
         roomVo.guest=user.cacheUserVo;
        client.currentUser.rednessModel.myRoom = roomVo;
          new RP_RoomEnterRspd(client,roomVo.roomID,roomVo.mapID,roomVo.ownner.guid,roomVo.pwd, roomVo.getUsers());
        new RP_RoomGameReadyRspd(client,roomVo.guestReady);
        if(roomVo.ownner.isRobot()==false&&roomVo.ownner.onlineUser!=null) {
            new RP_RoomEnterRspd(roomVo.ownner.onlineUser.client, roomVo.roomID, roomVo.mapID, roomVo.ownner.guid, roomVo.pwd, roomVo.getUsers());
        }
    }
}
