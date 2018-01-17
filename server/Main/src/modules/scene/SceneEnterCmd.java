package modules.scene;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import modules.passport.RedisTablePassport;
import protocol.*;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;


public class SceneEnterCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		SceneEnterRqst rqst= (SceneEnterRqst) baseRqst;
client.rtPassport.setFieldInt(RedisTablePassport.mapID,rqst.sceneID);

	 client.rtFightMap.setField(RedisTableFightMap.fightGroupIndex,"0",
			 RedisTableFightMap.fightGroupIndex,"0");

	new SceneEnterRspd(client,rqst.sceneID);

	}



}
