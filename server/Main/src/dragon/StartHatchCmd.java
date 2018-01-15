package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.StartHatchRqst;
import protocol.StartHatchRspd;
import table.DragonEggBaseVo;

/**
 * Created by admin on 2016/11/18.
 */
public class StartHatchCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        StartHatchRqst rqst = (StartHatchRqst)baseRqst;
        startHatch(client,user,rqst.tempID);
    }

    private void startHatch(Client client,User user,byte tempID){
        DragonEggVo dragonEggVo = user.cacheUserVo.dragonEggModel.getEggByTempID(tempID);
        if(dragonEggVo == null)return;
        DragonEggVo eggVo = user.cacheUserVo.dragonEggModel.getEggByTempID(tempID);
        if(eggVo == null)return;
        DragonEggBaseVo baseVo = Model.DragonEggBaseMap.get((int)eggVo.eggID);
        if(baseVo == null)return;
        dragonEggVo.time = JkTools.getGameServerTime(client) + baseVo.needTime;
//        dragonEggVo.time = JkTools.getGameServerTime() + 10 * 60;
        new StartHatchRspd(client,tempID,dragonEggVo.time);
        user.cacheUserVo.dragonEggModel.saveSqlData();
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        for(GangUserVo gangUserVo : gangVo.users.values()){
            if(gangUserVo.cacheUserVo.guid == user.guid)continue;
            if(gangUserVo.cacheUserVo.dragonEggModel.nowGangUserEggSet.contains(user.guid)){
                gangUserVo.cacheUserVo.dragonEggModel.theirsEggUpSet.add(user.guid);
            }
        }
    }
}
