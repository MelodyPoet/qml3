package modules.passport;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.*;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;


public class RoleCreateCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {

        RoleCreateRqst rqst=	(RoleCreateRqst)baseRqst;
		Jedis jedis= RedisClient.getOne();

		if(jedis.exists("passport:level:"+client.guid)){
			return;
		}
		jedis.set("passport:level:"+client.guid, 1+"");
		jedis.set("passport:uname:"+client.guid,rqst.name);
		new PassportRoleUpdateRspd(client,1,rqst.name);



 	}

}
