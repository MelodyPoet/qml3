package redness;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.RP_RoomEnterRspd;
import protocol.RP_RoomExitRqst;
import protocol.RP_RoomGameReadyRspd;
import protocol.ServerTipRspd;
import table.MapBaseVo;

/**
 * Created by admin on 2017/4/3.
 */
public class RP_QuickJoinCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RP_RoomVo roomVo = null;
        RP_RoomVo notEnterRoom = null;
        for (RP_RoomVo vo: RednessModel.allRooms.values()) {
            if(!"".equals(vo.pwd))continue;
            if(vo.guest != null)continue;
            if(user.notEnterRoom.contains(vo.roomID)){
                notEnterRoom = vo;
                continue;
            }
            if(!user.rednessModel.isCanEnter(vo.mapID)){
                new ServerTipRspd(client,(short)223,null);
                return;
            }
            if(vo.isInGame ==1)continue;
            MapBaseVo mapBaseVo = Model.MapBaseMap.get(vo.mapID);
            if(mapBaseVo.needLevel > user.cacheUserVo.level)continue;
            if(vo.ownner == null)continue;
            roomVo = vo;
            user.notEnterRoom.add(vo.roomID);
        }

        if(roomVo == null){
            if(notEnterRoom == null){
                new ServerTipRspd(client,(short)231,null);
                return;
            }else{
                if(notEnterRoom.ownner == null){
                    new ServerTipRspd(client,(short)231,null);
                    return;
                }
                user.notEnterRoom.clear();
                roomVo = notEnterRoom;
            }

        }

        if(user.rednessModel.myRoom!=null){
            RP_RoomExitRqst rp_roomExitRqst = new RP_RoomExitRqst();
            rp_roomExitRqst.isMe = true;
            new RP_RoomExitCmd().execute(client,user,rp_roomExitRqst);
        }
        if(roomVo.isInGame == 1)return;
        roomVo.needRobotTime = -1;
        roomVo.guest=user.cacheUserVo;
        client.currentUser.rednessModel.myRoom = roomVo;
        new RP_RoomEnterRspd(client,roomVo.roomID,roomVo.mapID,roomVo.ownner.guid,roomVo.pwd, roomVo.getUsers());
        new RP_RoomGameReadyRspd(client,roomVo.guestReady);
        //new RP_RoomEnterRspd(roomVo.ownner.client,roomVo.roomID,roomVo.mapID,roomVo.ownner.passportVo.guid,roomVo.pwd,roomVo.getUsers());
    }
}
