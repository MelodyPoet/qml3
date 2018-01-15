package prop;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DeletePropRspd;
import protocol.PropPVo;
import protocol.SellPropRqst;
import sqlCmd.AllSql;
import table.PropBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;


public class SellPropCmd extends BaseRqstCmd {
    PropPVo propPVo;
    PropBaseVo baseVo;
    public SellPropCmd(){};
public SellPropCmd(PropPVo propPVo,PropBaseVo baseVo){

    this.propPVo = propPVo;
    this.baseVo = baseVo;
}
    @Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
		SellPropRqst rqst=(SellPropRqst) baseRqst;
		   propPVo=user.propModel.getPropInBag(rqst.tempID);
			if(propPVo==null){
				return;
			}

		  baseVo = Model.PropBaseMap.get(propPVo.baseID);
		//if(user.actState== UserActState.CAMP){

           // DCGameLog.getInstance().AddLog(GameLogType.COINGAIN, new String[]{user.guid + "", UserDataEnum.MONEY + "", baseVo.price + "", rstVal + "", "sellItem"});
        //}


        executeFinal(client, user);
		//user.missionModel.progressBuyAct(MissionActEnum.SELL_EQUIP);
	}
    public void executeFinal(Client client,User user) {
        int rstVal=user.addUserData(UserDataEnum.MONEY, baseVo.price,true, ReasonTypeEnum.SELL_PROP);

        AllSql.propSql.delete(propPVo.tempID);
        user.propModel.deleteBag(propPVo.tempID,true,ReasonTypeEnum.SELL_PROP);

            new DeletePropRspd(client, propPVo.tempID);

    }
	 

}
