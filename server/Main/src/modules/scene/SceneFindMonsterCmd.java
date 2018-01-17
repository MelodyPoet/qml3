package modules.scene;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SceneEnterRqst;
import protocol.SceneEnterRspd;
import protocol.SceneFindMonsterRqst;
import protocol.SceneFindMonsterRspd;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;
import table.NpcLayoutBaseVo;


public class SceneFindMonsterCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		SceneFindMonsterRqst rqst= (SceneFindMonsterRqst) baseRqst;

	Jedis jedis=RedisClient.getOne();
int sceneID=Integer.parseInt(jedis.get("passport:mapID:"+client.guid));
		int fightIndex=Integer.parseInt(jedis.get("passport:fightIndex:"+client.guid));
		if(fightIndex>=Model.NpcLayoutBaseMap.get(sceneID).size()){
			// 通關

			return;

		}
	NpcLayoutBaseVo npcLayoutBaseVo= Model.NpcLayoutBaseMap.get(sceneID).get(fightIndex);
		int fightGroupIndex=Integer.parseInt(jedis.get("passport:fightGroupIndex:"+client.guid));
		if(fightGroupIndex<=0) {
			fightGroupIndex = npcLayoutBaseVo.countRange[0];
		}
	//if()


		fightGroupIndex--;
		jedis.set("passport:fightGroupIndex:"+client.guid,fightGroupIndex+"");
		new SceneFindMonsterRspd(client,fightIndex);
		if(fightGroupIndex<=0){
			fightIndex++;
			 jedis.set("passport:fightGroupIndex:"+client.guid,0+"");
			jedis.set("passport:fightIndex:"+client.guid, fightIndex+"");
		}



	}



}
