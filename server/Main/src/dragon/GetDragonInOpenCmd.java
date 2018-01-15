package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DragonPVo;

/**
 * Created by admin on 2017/5/15.
 */
public class GetDragonInOpenCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        short baseID = (short)user.baseVo.defaultDragon;
        DragonPVo dragonPVo= user.cacheUserVo.dragonCacheModel.dragonsMap.get(baseID);
        if (dragonPVo == null || !dragonPVo.isActive) {
            user.dragonModel.getOneInOpen();
        }
    }
}
