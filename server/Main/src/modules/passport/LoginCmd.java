package modules.passport;

import comm.*;
import gluffy.comm.BaseRqst;

import protocol.LoginRqst;
import protocol.LoginRspd;
import protocol.PassportRoleUpdateRspd;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;

// 负责验证账户密码
public class LoginCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
      LoginRqst rqst= (LoginRqst) baseRqst;

	Jedis jedis=RedisClient.getOne();

		//set the data in redis string
		boolean hasRole=false;
		if(jedis.exists("passport:level:"+client.guid)){
			System.out.println("old client" +jedis.get("passport:level:"+client.guid));
			new PassportRoleUpdateRspd(client,(byte)1,Integer.parseInt( jedis.get("passport:level:"+client.guid)),null,0,jedis.get("passport:uname:"+client.guid));
			hasRole=true;
		}else{
			System.out.println("new client");

		}


		client.passportLogined=true;

		new LoginRspd(client,hasRole?1:0,(byte)0,0,(byte)0);


	}



}
