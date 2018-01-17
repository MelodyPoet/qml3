package modules.scene;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import modules.passport.RedisTablePassport;
import protocol.SceneEnterRqst;
import protocol.SceneEnterRspd;
import protocol.SceneFindMonsterRqst;
import protocol.SceneFindMonsterRspd;
import qmshared.RedisClient;
import redis.clients.jedis.Jedis;
import table.NpcLayoutBaseVo;

import java.util.List;


public class SceneFindMonsterCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		SceneFindMonsterRqst rqst= (SceneFindMonsterRqst) baseRqst;


int sceneID=client.rtPassport.getFieldInt(RedisTablePassport.mapID);
	List<Integer> fightMap= client.rtFightMap.getFieldInt(RedisTableFightMap.layoutIndex,RedisTableFightMap.fightGroupIndex) ;
		int layoutIndex= fightMap.get(0);
		if(layoutIndex>=Model.NpcLayoutBaseMap.get(sceneID).size()){
			// 通關

			return;

		}
	NpcLayoutBaseVo npcLayoutBaseVo= Model.NpcLayoutBaseMap.get(sceneID).get(layoutIndex);
		int fightGroupIndex= fightMap.get(1);
		if(fightGroupIndex<=0) {
			fightGroupIndex = npcLayoutBaseVo.countRange[0];
		}
	//if()


		fightGroupIndex--;
		new SceneFindMonsterRspd(client,layoutIndex);
		if(fightGroupIndex<=0){
			layoutIndex++;
			client.rtFightMap.setField(RedisTableFightMap.layoutIndex,layoutIndex+"",RedisTableFightMap.fightGroupIndex,"0");

		}else{

			client.rtFightMap.setField(RedisTableFightMap.fightGroupIndex,fightGroupIndex+"");
		}



	}



}
