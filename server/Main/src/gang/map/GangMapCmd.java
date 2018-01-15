package gang.map;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.GangMapBaseVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/28.
 */
public class GangMapCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        new GangMapRspd(client,gangVo.mapModel.gangAllMap.values());
        MapModel mapModel = gangVo.mapModel;
        ArrayList<GangMapAwardPVo> list = new ArrayList<>();
        int now = JkTools.getGameServerTime(null);
        int[] dragonWish = gangVo.mapModel.dragonWish;
        for(Map.Entry<Short,HashMap<Long, GangMapAwardPVo>> item : mapModel.userMapAward.entrySet()){
            short baseID = item.getKey();
            GangMapPVo gangMapPVo = mapModel.gangAllMap.get(baseID);
            if(gangMapPVo == null)continue;
            GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get((int)gangMapPVo.mapID);
            HashMap<Long,GangMapAwardPVo> map = item.getValue();
            GangMapAwardPVo gangMapAwardPVo = null;
            if(map.containsKey(user.guid)){
                gangMapAwardPVo = map.get(user.guid);
                gangMapAwardPVo.num = gangMapBaseVo.limit*dragonWish[1]/100 - ((gangMapAwardPVo.nextTime - JkTools.getGameServerTime(client))/gangMapBaseVo.coolTime*dragonWish[3]/100+1)*gangMapBaseVo.count*dragonWish[2]/100;
            }else{
                gangMapAwardPVo = new GangMapAwardPVo();
                gangMapAwardPVo.baseID = baseID;
                gangMapAwardPVo.nextTime = now+gangMapBaseVo.limit*dragonWish[1]/100/gangMapBaseVo.count*dragonWish[2]/100*gangMapBaseVo.coolTime*dragonWish[3]/100;
                map.put(user.guid,gangMapAwardPVo);
            }
            list.add(gangMapAwardPVo);
        }
        new GangMapAwardRspd(client,list);
        ByteIntPVo dragon = gangVo.mapModel.gangDragon;
        new GangDragonRspd(client,dragon.value,dragon.key);
        gangVo.saveGangMap();
    }
}
