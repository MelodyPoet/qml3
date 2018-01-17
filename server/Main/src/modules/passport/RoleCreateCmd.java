package modules.passport;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.*;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;

import java.util.HashMap;


public class RoleCreateCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {

        RoleCreateRqst rqst=	(RoleCreateRqst)baseRqst;

		if(client.rtPassport.existTable()){
			return;
		}

		client.rtPassport.setField(RedisTablePassport.uname,rqst.name,RedisTablePassport.level,"1");
		new PassportRoleUpdateRspd(client,1,rqst.name);



 	}

}
