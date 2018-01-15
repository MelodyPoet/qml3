package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingCoreAdvanceRqst;
import protocol.WingCorePVo;
import protocol.WingCoreRspd;
import table.ReasonTypeEnum;
import table.WingCoreAdvanceBaseVo;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/27.
 */
public class WingCoreAdvanceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WingCoreAdvanceRqst rqst = (WingCoreAdvanceRqst)baseRqst;
        WingModel wingModel = user.cacheUserVo.wingModel;
        WingCorePVo wingCorePVo = wingModel.coreMap.get(rqst.id);
        ArrayList<WingCoreAdvanceBaseVo> list = Model.WingCoreAdvanceBaseMap.get((int)rqst.id);
        if(list == null || wingCorePVo.advance >= list.size()-1)return;
        WingCoreAdvanceBaseVo wingCoreAdvanceBaseVo = list.get(wingCorePVo.advance);
        if(user.costUserDataAndPropList(wingCoreAdvanceBaseVo.costData,true, ReasonTypeEnum.WING_ADVANCE,null) == false)return;
        wingCorePVo.advance++;
        wingModel.saveSqlData();
        user.updateZDL();
        new WingCoreRspd(client,(byte) 2,wingCorePVo);
    }
}
