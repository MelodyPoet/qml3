package redness;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.RP_RoomCreateRqst;
import protocol.RP_RoomEnterRspd;
import protocol.ServerTipRspd;
import table.MapBaseVo;

/**
 * Created by admin on 2016/11/18.
 */
public class RP_RoomCreateCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RP_RoomCreateRqst rqst = (RP_RoomCreateRqst)baseRqst;

//        if(user.rednessModel.myRoom!=null){
//            long time = System.currentTimeMillis() - client.lastRqstTime;
//            if(time > 6000 * 5){
//                user.rednessModel.myRoom = null;
//            }else{
//                return;
//            }
//        }
        if(user.rednessModel.myRoom!=null)return;
        if(!user.rednessModel.isCanEnter(rqst.mapID)){
            new ServerTipRspd(client,(short)223,null);
            return;
        }

        MapBaseVo mapBaseVo = Model.MapBaseMap.get(rqst.mapID);
        if(mapBaseVo.needLevel > user.cacheUserVo.level){
            new ServerTipRspd(client,(short)24,null);
            return;
        }

        RP_RoomVo roomVo=new RP_RoomVo();
        roomVo.pwd=rqst.pwd;
        roomVo.roomID=++RednessModel.tempID;
        roomVo.ownner=user.cacheUserVo;
        roomVo.mapID=rqst.mapID;
        roomVo.needRobotTime = JkTools.getGameServerTime(client)+RednessModel.NEED_ROBOT_TIME;

        client.currentUser.rednessModel.myRoom=roomVo;

        RednessModel.allRooms.put(roomVo.roomID,roomVo);
        user.rednessModel.myRoom = roomVo;


       new RP_RoomEnterRspd(client,roomVo.roomID,roomVo.mapID,roomVo.ownner.guid,roomVo.pwd,roomVo.getUsers());
        //test robot

        //robotList[0];


    }
}
