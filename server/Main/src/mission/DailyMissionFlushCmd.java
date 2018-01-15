package mission;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;


public class DailyMissionFlushCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
      // if( user.missionModel.progressBuyAct(MissionActEnum.FLUSH_DAILY_MISSION))return;


//
//        DailyMissionBaseVo dailyMissionBaseVo=      Model.DailyMissionBaseMap.get(user.getUserData(UserDataEnum.LEVEL));
//  int lastFree=user.getUserData(UserDataEnum.DAILY_MISSION_FREE);
//    if(lastFree>0){
//        lastFree--;
//      user.setUserData(UserDataEnum.DAILY_MISSION_FREE,lastFree,true);
//
//    }else{
//        if(user.costUserDataAndProp(UserDataEnum.DIAMOND,dailyMissionBaseVo.price,true)==false){
//            new ServerTipRspd(client,(short)17,null);
//
//            return;
//        }
//
//    }
//
//user.missionModel.flushDaily();

    }

	 

}
