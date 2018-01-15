package friend;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.FriendSysInfoPVo;
import protocol.SystemInfoListRqst;
import protocol.SystemInfoListRspd;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/8.
 */
public class SystemInfoListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SystemInfoListRqst rqst = (SystemInfoListRqst)baseRqst;
        int startIndex = rqst.startIndex;
        if (startIndex < 0) startIndex = 0;
        ArrayList<FriendSysInfoVo> sysInfoList = user.friendModel.sysInfoList;
        ArrayList<FriendSysInfoPVo> infos = new ArrayList<>();
        if (startIndex < sysInfoList.size()){
            for (int i = 0; i < 10 && startIndex+i<sysInfoList.size(); i++) {
                FriendSysInfoVo friendSysInfoVo = sysInfoList.get(startIndex+i);
                if(friendSysInfoVo == null)break;
                FriendSysInfoPVo friendSysInfoPVo = new FriendSysInfoPVo();
                friendSysInfoPVo.type = friendSysInfoVo.type;
                friendSysInfoPVo.guid = friendSysInfoVo.attentionID;
                friendSysInfoPVo.time = friendSysInfoVo.createTime;
                friendSysInfoPVo.name = friendSysInfoVo.uname;
                friendSysInfoPVo.isAttention = FriendModel.isMyAttention(user.guid,friendSysInfoVo.attentionID);
                infos.add(friendSysInfoPVo);
            }
            long guid = sysInfoList.get(0).guid;
            if(user.friendModel.lastSysInfoID != guid){
                user.friendModel.lastSysInfoID = guid;
                user.friendModel.saveSqlData();
            }
        }
        user.cacheUserVo.newSysInfoCount = 0;
        new SystemInfoListRspd(client,startIndex,infos,sysInfoList.size());
    }
}
