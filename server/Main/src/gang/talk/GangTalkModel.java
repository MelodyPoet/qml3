package gang.talk;

import comm.Client;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.utils.JkTools;
import protocol.GangTalkBackRspd;
import protocol.GangTalkMsgPVo;
import table.TalkEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2016/7/12.
 */
public class GangTalkModel {
    public static final int TimeListCount = 20;
    public  ArrayList<GangTalkMsgPVo> talkList = new ArrayList<>();


    public void addTalkMsgInTimeList(User user, byte type, String msg) {
        if(type == TalkEnum.GANG){
            if(user.cacheUserVo.gang.addTalkList.size()>0){
                for(GangTalkMsgPVo talkMsgPVo : user.cacheUserVo.gang.addTalkList){
                    new GangTalkBackRspd(user.client, talkMsgPVo);
                }
                user.cacheUserVo.gang.addTalkList.clear();
            }
            return;
        }
        GangTalkMsgPVo vo = new GangTalkMsgPVo();
        vo.userID = user.guid;
        vo.name = user.cacheUserVo.name;
        vo.office = user.cacheUserVo.gang.gangVo.users.get(user.guid).office;
        vo.type = type;
        vo.msg = msg;
        vo.time = JkTools.getGameServerTime(null);
        addTalkMsg(vo);
        gangTalkBack(user.guid,user.cacheUserVo.gang.gangVo, vo);
    }

    public void addTalkMsg(GangTalkMsgPVo vo) {
        if (talkList.size() >= TimeListCount) {
            talkList.remove(0);
        }
        talkList.add(vo);
    }

    public void gangTalkBack(Long userID,GangVo gangVo, GangTalkMsgPVo vo) {
        for (GangUserVo gangUserVo : gangVo.users.values()) {
            if(userID == gangUserVo.cacheUserVo.guid)continue;
            Client client = Client.getOne(gangUserVo.cacheUserVo);
            if(client != null && client.currentUser.guid == gangUserVo.cacheUserVo.guid){
                gangUserVo.cacheUserVo.gang.addTalkList.add(vo);
            }
        }
    }
}
