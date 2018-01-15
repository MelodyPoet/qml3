package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingUnlockRqst;
import protocol.WingUnlockRspd;
import table.MissionBaseVo;
import table.MissionTypeEnum;
import table.ReasonTypeEnum;
import table.WingBaseVo;

/**
 * Created by admin on 2017/7/27.
 */
public class WingUnlockCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WingUnlockRqst rqst = (WingUnlockRqst)baseRqst;
        WingBaseVo wingBaseVo = Model.WingBaseMap.get((int)rqst.id);
        WingModel wingModel = user.cacheUserVo.wingModel;
        if(wingModel.wingSet.contains(rqst.id))return;
        if(wingBaseVo.needMission != null){
            for(int missionID : wingBaseVo.needMission){
                MissionBaseVo missionBaseVo=Model.MissionBaseMap.get(missionID);
                if(missionBaseVo==null)return;
                if(missionBaseVo.type != MissionTypeEnum.ACHIEVE)return;
                if(user.achieveModel.canComplete(missionBaseVo) >= 0)return;
            }
        }
        if(user.costUserDataAndPropList(wingBaseVo.costItems,true, ReasonTypeEnum.WING_UNLOCK,null) == false)return;
        wingModel.wingSet.add(rqst.id);
        wingModel.saveSqlData();
        user.updateZDL();
        new WingUnlockRspd(client,rqst.id);
    }
}
