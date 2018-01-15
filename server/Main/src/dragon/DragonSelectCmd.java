package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DragonPVo;
import protocol.DragonSelectRqst;
import protocol.DragonSelectRspd;


public class DragonSelectCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
//        user.dragonModel.flushDragonList(client);
        DragonSelectRqst rqst = (DragonSelectRqst) baseRqst;
        DragonCacheModel dragonCacheModel = user.cacheUserVo.dragonCacheModel;
        if(rqst.baseID==0){
            dragonCacheModel.mainDragon=null;
            new DragonSelectRspd(client,0);
            dragonCacheModel.saveSqlData();
            return;
        }
        DragonPVo dragonPVo= dragonCacheModel.dragonsMap.get(rqst.baseID);
        if(dragonPVo==null)return;
        dragonCacheModel.mainDragon=dragonPVo;
        new DragonSelectRspd(client,dragonPVo.baseID);
        dragonCacheModel.saveSqlData();
        user.updateZDL();
    }

	 

}
