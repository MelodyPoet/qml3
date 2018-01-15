package mine;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.MR_PublicMineRobotRspd;
import protocol.TeamUserPVo;
import utils.UserVoAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/15.
 */
public class MR_PublicMineRobotCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ArrayList<TeamUserPVo> list = new ArrayList<>();
        user.cacheUserVo.mineModel.publicTeamMap.clear();
//        for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
        for(CacheUserVo cacheUserVo : user.cacheUserVo.mineModel.myRoom.roomUsers.values()){
            if(cacheUserVo.guid == user.guid)continue;
            list.add(UserVoAdapter.toTeamUserPVo(cacheUserVo));
            user.cacheUserVo.mineModel.publicTeamMap.add(cacheUserVo);
            if(list.size() >= 3)break;
        }
        new MR_PublicMineRobotRspd(client,list);
    }
}
