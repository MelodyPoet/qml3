package redness;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.RP_RoomListRqst;
import protocol.RP_RoomListRspd;
import protocol.RP_RoomPVo;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/18.
 */
public class RP_RoomListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RP_RoomListRqst rqst = (RP_RoomListRqst)baseRqst;

        //client.currentUser.rednessModel.myRoom=null;


        ArrayList<RP_RoomPVo> roomPvos=new ArrayList<>();
        for (RP_RoomVo vo: RednessModel.allRooms.values()) {
            RP_RoomPVo pvo=new RP_RoomPVo();
            pvo.mapID=vo.mapID;
            pvo.roomID=vo.roomID;
            pvo.userCount=vo.isInGame == 1?-1:vo.guest==null?(byte)1:(byte)2;
            pvo.pwd = vo.pwd;
            roomPvos.add(pvo);

        }
        new RP_RoomListRspd(client,roomPvos);

    }
}
