package base;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.*;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;
import table.ShieldBaseVo;
import table.ShopBaseVo;


public class UserChangeNameCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        UserChangeNameRqst rqst= (UserChangeNameRqst) baseRqst;

       // if(client.passportVo.name!=null)return ;
if(canNamed(rqst.name,client)==false)return;
        ShopBaseVo shopVo= Model.ShopBaseMap.get(2000);
        if(user.costUserDataAndPropList(shopVo.costUserdata,true, ReasonTypeEnum.CHANGE_NAME, null)==false)return;

        AllSql.userSql.update(user, AllSql.userSql.FIELD_NAME, "\'" + rqst.name + "\'");
        user.cacheUserVo.name=rqst.name;
        CacheUserVo.usedName.add(rqst.name);

         new GeneralSuccessRspd(client,baseRqst.protocolID);
    }

	 
public  static  boolean canNamed(String name,Client client){
    if(CacheUserVo.usedName.contains(name)){
        new ServerTipRspd(client,(short)56,null);
        return false;
    }
    if (!CheckUtils.checkName(name)) {
        new ServerTipRspd(client,(short)120,null);
        return false;
    }
    for(ShieldBaseVo vo: Model.ShieldBaseMap.values()){
        if(name.indexOf(vo.name) != -1){
            new ServerTipRspd(client,(short)120,null);
            return false;
        }
    }
    return  true;
}
}
