package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingCorePVo;
import protocol.WingCoreRspd;
import protocol.WingCoreUnlockRqst;
import table.WingCoreBaseVo;

/**
 * Created by admin on 2017/7/28.
 */
public class WingCoreUnlockCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WingCoreUnlockRqst rqst = (WingCoreUnlockRqst)baseRqst;
        WingCoreBaseVo wingCoreBaseVo = Model.WingCoreBaseMap.get((int)rqst.id);
        WingModel wingModel = user.cacheUserVo.wingModel;
        if(wingCoreBaseVo == null)return;
        if(user.cacheUserVo.level < wingCoreBaseVo.needLevel)return;
        if(wingCoreBaseVo.conditionParams != null){
            for(int i=0;i<wingCoreBaseVo.conditionParams.length/3;i++){
                int base = i*3;
                WingCorePVo wingCorePVo = wingModel.coreMap.get((byte)wingCoreBaseVo.conditionParams[base]);
                if(wingCorePVo == null)return;
                switch (wingCoreBaseVo.conditionParams[base+1]){
                    case 1:
                        if(wingCorePVo.level < wingCoreBaseVo.conditionParams[base+2])return;
                        break;
                    case 2:
                        if(wingCorePVo.advance < wingCoreBaseVo.conditionParams[base+2])return;
                        break;
                }
            }
        }
        WingCorePVo wingCorePVo = new WingCorePVo();
        wingCorePVo.level = 1;
        wingModel.coreMap.put(rqst.id,wingCorePVo);
        wingModel.saveSqlData();
        user.updateZDL();
        new WingCoreRspd(client,(byte)0,wingCorePVo);
    }
}
