package gang.map;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangMapInfoRqst;
import protocol.GangMapInfoRspd;
import protocol.GangMapPVo;
import table.GangMapBaseVo;

/**
 * Created by admin on 2017/4/27.
 */
public class GangMapInfoCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangMapInfoRqst rqst = (GangMapInfoRqst)baseRqst;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        MapModel mapModel = gangVo.mapModel;
        short baseID = rqst.baseID;
        GangMapPVo gangMapPVo = new GangMapPVo();
        if(mapModel.gangAllMap.containsKey(baseID)){
            gangMapPVo = mapModel.gangAllMap.get(baseID);
        }else{
            GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get((int)baseID);
            gangMapPVo.baseID = baseID;
            gangMapPVo.mapID = baseID;
            gangMapPVo.bossBlood = gangMapBaseVo.bossBlood;
            gangMapPVo.level = (byte)0;
            gangMapPVo.userCount = 0;
        }
        new GangMapInfoRspd(client,gangMapPVo);
    }
}
