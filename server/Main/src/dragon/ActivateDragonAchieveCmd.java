package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ActivateDragonAchieveRqst;
import protocol.ActivateDragonAchieveRspd;
import protocol.DragonPVo;
import table.DragonAchieveBaseVo;

import java.util.Map;

/**
 * Created by admin on 2017/3/30.
 */
public class ActivateDragonAchieveCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ActivateDragonAchieveRqst rqst = (ActivateDragonAchieveRqst) baseRqst;
        DragonAchieveBaseVo baseVo = Model.DragonAchieveBaseMap.get((int)rqst.achieveID);
        if(baseVo == null)return;
        if(user.dragonModel.dragonAchieveSet.contains(rqst.achieveID))return;
        Map<Short, DragonPVo> dragonMap = user.cacheUserVo.dragonCacheModel.dragonsMap;
        for(Map.Entry<Integer,Integer> item : baseVo.condition.entrySet()){
            short dragonID = (short)(int)item.getKey();
            if(!dragonMap.containsKey(dragonID))return;
            DragonPVo dragonPVo = dragonMap.get(dragonID);
            if(dragonPVo.isActive && dragonPVo.advance < item.getValue())return;
        }
        user.dragonModel.dragonAchieveSet.add(rqst.achieveID);
        user.dragonModel.saveSqlData();
        new ActivateDragonAchieveRspd(client,rqst.achieveID);
    }
}
