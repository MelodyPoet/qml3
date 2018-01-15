package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.CountersignRqst;
import protocol.MissionCompleteRspd;
import protocol.ServerTipRspd;
import table.MissionBaseVo;
import table.ReasonTypeEnum;

/**
 * Created by admin on 2016/9/5.
 */
public class CountersignCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        CountersignRqst rqst = (CountersignRqst) baseRqst;
        checkCounterSign(client,user,rqst.countersign);
    }

    private void checkCounterSign(Client client, User user,String countersign){
        if(user.achieveModel.completedList.contains(901))return;
        if(Model.COUNTERSIGN.equals(countersign)){
            MissionBaseVo missionBaseVo=Model.MissionBaseMap.get(901);
            if(missionBaseVo==null){
                new ServerTipRspd(client,(short)121,null);
                return;
            }
            user.achieveModel.completedList.add(missionBaseVo.ID);

//            for(int i=0;i<missionBaseVo.prop.length;i+=2){
//                int whiteID = missionBaseVo.prop[i];
//                if(PropModel.isEquip(whiteID)){
//                    whiteID=whiteID%1000+user.cacheUserVo.baseID*1000;
//                    missionBaseVo.prop[i] = whiteID;
//                }
//            }
            user.propModel.addListToBag(missionBaseVo.prop, ReasonTypeEnum.COUNTER_SIGN);

            new MissionCompleteRspd(client,missionBaseVo.ID);
        }else{
            new ServerTipRspd(client,(short)121,null);
        }
        user.achieveModel.saveSqlData();
    }
}
