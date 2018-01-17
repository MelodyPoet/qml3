package modules.scene;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;


public class SceneEnterCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		SceneEnterRqst rqst= (SceneEnterRqst) baseRqst;

	Jedis jedis=RedisClient.getOne();
	jedis.set("passport:mapID:"+client.guid, rqst.sceneID+"");
		jedis.set("passport:fightIndex:"+client.guid, 0+"");
		jedis.set("passport:fightGroupIndex:"+client.guid,0+"");
	new SceneEnterRspd(client,rqst.sceneID);

	}



}
