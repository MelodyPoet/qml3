package mine;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gameset.GameSetModel;
import gluffy.comm.BaseRqst;
import protocol.MR_PublicMineRspd;
import protocol.ServerTipRspd;

/**
 * Created by admin on 2017/7/14.
 */
public class MR_PublicMineCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(!GameSetModel.checkTime(17)){
            new ServerTipRspd(client,(short)359,null);
            return;
        }
        MineModel mineModel = user.cacheUserVo.mineModel;
        MineModel.flushPublicMine(mineModel.myRoom);
        new MR_PublicMineRspd(client,mineModel.myRoom.publicMap.values());
    }
}
