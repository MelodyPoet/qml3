package gang.divination;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gameset.GameSetModel;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GiftLogRspd;

/**
 * Created by admin on 2017/4/14.
 */
public class GiftLogCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GameSetModel.checkTime(1);
        new GiftLogRspd(client,gangVo.giftLogList);
    }
}
