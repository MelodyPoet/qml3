package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GoTowerRqst;
import protocol.GoTowerRspd;
import table.MapTypeEnum;
import table.MissionConditionEnum;
import table.TowerBaseVo;
import table.UserDataEnum;


public class GoTowerCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        if(user.propModel.checkBagIsFull())return;
        GoTowerRqst rqst=(GoTowerRqst) baseRqst;
        if(user.wormNestModel.isCanChallenge()==false)return;

       int currentFloor=user.getUserData(UserDataEnum.WormNestCurrentFloor);

         TowerBaseVo myTower=Model.TowerBaseMap.get(rqst.towerID).get(currentFloor);

        new GoTowerRspd(client,rqst.towerID);
        new GoMapCmd().realDoToTower(client,user,rqst.towerID,currentFloor,true);
        user.actState = UserActState.FIGHT;
        user.activationModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON, MapTypeEnum.TWOER);
	}

}
