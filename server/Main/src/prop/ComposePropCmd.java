package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AddPropRspd;
import protocol.ComposePropRqst;
import protocol.PropPVo;
import table.PropCellEnum;
import table.PropComposeBaseVo;
import table.ReasonTypeEnum;

import java.util.HashSet;


public class ComposePropCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        ComposePropRqst rqst=(ComposePropRqst) baseRqst;
        PropComposeBaseVo vo = Model.PropComposeBaseMap.get(rqst.propID);
        if(vo == null)return;
        if(vo.costdata == null && vo.needProp == null)return;
        HashSet<Long> stoneTempIDList = new HashSet<>();
        int count = 0;
        for (int i = 0,len=rqst.type==1?1000:1; i <len; i++) {
            if(user.enoughForcostUserDataAndProp(vo.costdata) == false)break;
            if(user.costUserDataAndPropList(vo.needProp,false, ReasonTypeEnum.COMPOSE_PROP,stoneTempIDList) == false)break;
            if(user.costUserDataAndPropList(vo.costdata,true,ReasonTypeEnum.COMPOSE_PROP,null) == false)break;
            count++;
        }
        if(count == 0)return;
        for (long tempID : stoneTempIDList) {
            if (tempID <= 0) continue;
            user.propModel.updateInBag(tempID, true, true);
        }
        PropPVo newPvo=new PropPVo();
        newPvo.baseID= rqst.propID;
        newPvo.count=count;
        newPvo=  user.propModel.addInBag(newPvo,true,true,ReasonTypeEnum.COMPOSE_PROP);
        new AddPropRspd(client, PropCellEnum.BAG,newPvo);

 	}

	 

}
