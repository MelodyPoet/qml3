package mission;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.MissionCmpActRqst;
import protocol.MissionProcessRspd;
import table.MissionBaseVo;
import table.MissionConditionEnum;


public class MissionCmpActCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        MissionCmpActRqst rqst = (MissionCmpActRqst) baseRqst;
        MissionBaseVo missionBaseVo=Model.MissionBaseMap.get(rqst.msID);
        for (MissionBaseVo msVo : user.missionModel.acceptedList.keySet()) {
            if(msVo.ID==rqst.msID&&msVo.conditionType==MissionConditionEnum.ALL_CLIENT_AUTO&&   user.missionModel.acceptedList.get(msVo)==0){
                user.missionModel.acceptedList.put(msVo,1);
                new MissionProcessRspd(user.client,msVo.ID, (short)1);
                user.cacheUserVo.skillModel.saveSqlData();
                return;
            }
        }


    }

	 

}
