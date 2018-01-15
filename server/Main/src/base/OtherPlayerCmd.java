package base;

 import comm.*;
import gluffy.comm.BaseRqst;
import protocol.OtherPlayerRqst;
import protocol.OtherPlayerRspd;
 import utils.UserVoAdapter;


public class OtherPlayerCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        OtherPlayerRqst rqst = (OtherPlayerRqst) baseRqst;
        if(rqst.userID == user.guid)return;
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.userID);
        if(cacheUserVo == null)return;
        new OtherPlayerRspd(client,UserVoAdapter.toOtherUserPVo(cacheUserVo,user.cacheUserVo));
    }
}
