package base;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.*;
import sqlCmd.AllSql;


public class RoleCreateCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {

        RoleCreateRqst rqst=	(RoleCreateRqst)baseRqst;
		//if(user!=null)return;
       if( Model.RoleBaseMap.containsKey((int)rqst.baseID)==false)return;
        if (UserChangeNameCmd.canNamed(rqst.name, client) == false) return;
        user=User.createOne(client, rqst.baseID);
        if(user==null){
            return;
        }
        if(user.cacheUserVo.name==null) {
            AllSql.userSql.update(user, AllSql.userSql.FIELD_NAME, "\'" + rqst.name + "\'");
            user.cacheUserVo.name = rqst.name;
            CacheUserVo.usedName.add(rqst.name);
        }

        client.userMap.put(user.baseID,user);
        client.currentUser=null;

        LoginRoleRqst lgRqst=new LoginRoleRqst();
        lgRqst.baseID=rqst.baseID;
 new LoginRoleCmd().execute(client,user,lgRqst);
      //new PassportRoleUpdateRspd(client,rqst.baseID,rqst.name,1,null);
 	}

}
