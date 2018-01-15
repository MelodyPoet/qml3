package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AddPropRspd;
import protocol.ComposeRqst;
import protocol.PropPVo;
import table.PropComposeBaseVo;
import table.PropCellEnum;
import table.ReasonTypeEnum;


public class ComposeCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        ComposeRqst rqst=(ComposeRqst) baseRqst;
         PropPVo mainProp=null;

            mainProp=user.propModel.getPropInBag(rqst.bagTempID);

         if( mainProp==null)return;

       PropComposeBaseVo propComposeBaseVo = Model.PropComposeBaseMap.get(mainProp.baseID);
        if(propComposeBaseVo ==null)return;
        if(propComposeBaseVo.products == 0)return;

       int addCount= mainProp.count/ propComposeBaseVo.needCount;
        if(addCount<=0)return;
        if(rqst.type != 1){
            addCount = 1;
        }


  user.costUserDataAndProp(mainProp.baseID,propComposeBaseVo.needCount*addCount,true, ReasonTypeEnum.COMPOSE_PROP);
PropPVo newPvo=new PropPVo();
        newPvo.baseID= propComposeBaseVo.products;
        newPvo.count=addCount;
        newPvo=  user.propModel.addInBag(newPvo,true,true,ReasonTypeEnum.COMPOSE_PROP);
        new AddPropRspd(client, PropCellEnum.BAG,newPvo);

 	}

	 

}
