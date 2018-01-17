package modules.passport;

import comm.*;
import gluffy.comm.BaseRqst;

import protocol.LoginRqst;
import protocol.LoginRspd;
import protocol.PassportRoleUpdateRspd;
import protocol.SceneEnterRspd;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;

import java.util.List;

// 负责验证账户密码
public class LoginCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
      LoginRqst rqst= (LoginRqst) baseRqst;


		//set the data in redis string
		boolean hasRole=false;
		if(client.rtPassport.existTable()){
		List<String> loadData= client.rtPassport.getField(RedisTablePassport.level,RedisTablePassport.uname);

			System.out.println("old client" +loadData.get(0));

			new PassportRoleUpdateRspd(client,Integer.parseInt( loadData.get(0)),loadData.get(1));
			hasRole=true;
		}else{
			System.out.println("new client");

		}


		client.passportLogined=true;

		new LoginRspd(client,hasRole,(byte)0,0,(byte)0);

		new SceneEnterRspd(client,1);

	}



}
