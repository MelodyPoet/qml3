//package redness;
//
//import comm.CacheUserVo;
//import gluffy.udp.core.BaseRqst;
//import gluffy.udp.protocol.RP_GoMapRqst;
//import gluffy.udp.protocol.MltGoMapRspd;
//import gluffy.udp.scenecast.CastModel;
//
//import java.util.ArrayList;
//
//
//public class RP_GoMapCmd extends BaseRqstCmd {
//
//	@Override
//	public void execute(UdpClient client, BaseRqst baseRqst) {
//		RP_GoMapRqst rqst = (RP_GoMapRqst) baseRqst;
//client.user.rednessModel.udpisReady=true;
//		RP_RoomVo roomVo = client.user.rednessModel.myRoom;
//	CacheUserVo teamerCuvo= roomVo.getTeamer(client.user.cacheUserVo);
//		if(teamerCuvo.isRobot()){
//			 client.roomClientList = new ArrayList<>();
//			client.roomClientList.add(client);
//
//			startRoom(client);
//			client.user.rednessModel.udpisReady = false;
// 		}else {
//			UdpClient teamerCl = UdpClient.getOne(teamerCuvo.passportVo.devID, client.serverID);
//			if (teamerCl == null) return;
//			if (teamerCl.user.rednessModel.udpisReady) {
//				//UdpClient.getOne()
//				teamerCl.roomClientList = client.roomClientList = new ArrayList<>();
//				client.roomClientList.add(client);
//				client.roomClientList.add(teamerCl);
//
//				startRoom(client);
//				client.user.rednessModel.udpisReady = false;
//				teamerCl.user.rednessModel.udpisReady = false;
//			}
//		}
//
//    }
//
//    public  void startRoom(UdpClient client){
//
//		new MltGoMapRspd(null,client.user.rednessModel.myRoom.getSkillUsers(),client.user.rednessModel.myRoom.mapID,0,0,0);
//		CastModel.castInRoom(client,null);
//
//	}
//
//
//}
