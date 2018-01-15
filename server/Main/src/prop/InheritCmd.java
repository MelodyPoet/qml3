package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.InheritRqst;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.EquipInheritBaseVo;
import table.PropBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;


public class InheritCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        InheritRqst rqst=(InheritRqst) baseRqst;


      PropPVo toMainProp=null,fromMainProp=null;
        if(rqst.toEquipIndex>-1) {
            toMainProp=user.propModel.getEquipByIndex(rqst.toEquipIndex);
        }else{
            toMainProp=user.propModel.getPropInBag(rqst.toBagTempID);
        }

        if( toMainProp==null||!PropModel.isEquip(toMainProp.baseID))return;

        if(rqst.fromEquipIndex>-1) {
            fromMainProp=user.propModel.getEquipByIndex(rqst.fromEquipIndex);
        }else{
            fromMainProp=user.propModel.getPropInBag(rqst.fromBagTempID);
        }

        if( fromMainProp==null||!PropModel.isEquip(fromMainProp.baseID))return;

        if(fromMainProp.intensify<=toMainProp.intensify)return;
        PropBaseVo toMainBase= Model.PropBaseMap.get(toMainProp.baseID);
          PropBaseVo fromMainBase= Model.PropBaseMap.get(fromMainProp.baseID);
if(toMainBase.quality!=fromMainBase.quality||toMainBase.type!=fromMainBase.type){
    return;
}
       EquipInheritBaseVo equipInheritBaseVo = Model.EquipInheritBaseMap.get((int)fromMainProp.intensify);
        if(user.costUserDataAndProp(UserDataEnum.DIAMOND, equipInheritBaseVo.costItems[toMainBase.quality],true, ReasonTypeEnum.INHERIT)==false)return;

        toMainProp.intensify=  fromMainProp.intensify;
        fromMainProp.intensify=0;
        AllSql.propSql.update(AllSql.propSql.FIELD_INTENSIFY, fromMainProp.intensify + "", fromMainProp.tempID);
        AllSql.propSql.update(AllSql.propSql.FIELD_INTENSIFY, toMainProp.intensify + "", toMainProp.tempID);
        new GeneralSuccessRspd(client,rqst.protocolID);

        if(rqst.toEquipIndex>-1) {
            user.updateZDL();
        }
 	}

	 

}
