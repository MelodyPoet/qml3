package talk;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.RedPacketsInfoRspd;
import protocol.TalkLikeRqst;
import protocol.TalkMsgPVo;
import protocol.TalkRedPacketUserPVo;
import sqlCmd.AllSql;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;
import table.TalkEnum;
import table.UserDataEnum;
import utils.UserVoAdapter;


public class TalkLikeCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        TalkLikeRqst rqst = (TalkLikeRqst) baseRqst;

        TalkMsgPVo talkMsgPVo=TalkModel.talkMsgMap.get(rqst.ID);
        if(talkMsgPVo == null)return;
        if(talkMsgPVo.type== TalkEnum.REDPACKET){
            if(rqst.ID == -1){
                user.addUserData(UserDataEnum.DIAMOND, Model.GameSetBaseMap.get(76).intValue,true,ReasonTypeEnum.RED_PACKET);
                return;
            }
            TalkRedPacketVo packetVo= TalkModel.redPacketMap.get(rqst.ID);
            if(packetVo==null)return;
            if(packetVo.diamondList.size()>0&&  packetVo.allGet.containsKey(user.guid)==false) {
                int diamondIndex = (int) (Math.random() * packetVo.diamondList.size());
                int val = packetVo.diamondList.get(diamondIndex);
                packetVo.diamondList.remove(diamondIndex);
                user.addUserData(UserDataEnum.DIAMOND, val, true, ReasonTypeEnum.RED_PACKET);
                packetVo.allGet.put(user.guid,val);
                TalkRedPacketUserPVo redpacketUserPVo = UserVoAdapter.toTalkRedPacketUserPVo(user.cacheUserVo, val);
                if(packetVo.users.size()==0) {
                    packetVo.users.add(0, redpacketUserPVo);
                }
                packetVo.users.add(1,redpacketUserPVo);
                if(val>packetVo.users.get(0).diamond){
                    packetVo.users.set(0,redpacketUserPVo);
                }
                if(packetVo.users.size()>11){
                    packetVo.users.remove(11);
                }
                TalkModel.saveRedPacket(talkMsgPVo,packetVo);
if(packetVo.diamondList.size()==0){
    talkMsgPVo.likes=-1;
    AllSql.worldTreeSql.update(talkMsgPVo,AllSql.worldTreeSql.FIELD_LIKES,talkMsgPVo.likes);
}

            }
           Integer myGet= packetVo.allGet.get(user.guid);
            if(myGet==null)myGet=0;

new RedPacketsInfoRspd(client,talkMsgPVo.ID,talkMsgPVo.portrait,talkMsgPVo.roleName,packetVo.totalDiamond,(byte)(0),(short)(packetVo.totalCount-packetVo.diamondList.size()),(short)(packetVo.totalCount),talkMsgPVo.msg,packetVo.users,myGet);


        }else {
            TalkModel.makeLike(rqst.ID,user);
            user.activationModel.progressBuyAct(MissionConditionEnum.WORLD_TREE_LIKE, 0);
            user.addUserData(UserDataEnum.LJ_LIKE_TALK_COUNT,1,true);
        }
    }
	 

}
