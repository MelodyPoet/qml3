package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.BuyAndUsePropRqst;
import table.PropBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;


public class BuyAndUsePropCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        BuyAndUsePropRqst rqst=(BuyAndUsePropRqst) baseRqst;
        if(rqst.count<=0)rqst.count=1;

	PropBaseVo baseVo = Model.PropBaseMap.get(rqst.baseID);
	if(user.costUserDataAndProp(UserDataEnum.DIAMOND, Model.AutoBuyBaseMap.get(rqst.baseID).diamond*rqst.count,true, ReasonTypeEnum.BUY_AND_USE_PROP)==false)return;
       new UseBagPropCmd().execute(client,user,baseVo,rqst.count);
    }
	 
		


	 

}
