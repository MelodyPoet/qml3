package redness;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.RP_GangmenberListRspd;
import protocol.SimpleUserPVo;
import table.FunctionOpenBaseVo;
import table.MapBaseVo;
import table.ModuleUIEnum;
import utils.UserVoAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/24.
 */
public class RP_GangmenberListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        ArrayList<SimpleUserPVo > users=new ArrayList<>();
        for(GangUserVo gangUserVo : gangVo.users.values()){
            Client client1 = Client.getOne(gangUserVo.cacheUserVo);
            if(client1 == null || client1.currentUser == null || client1.currentUser.cacheUserVo.guid != gangUserVo.cacheUserVo.guid)continue;
            MapBaseVo mapBaseVo = Model.MapBaseMap.get(user.rednessModel.myRoom.mapID);
            FunctionOpenBaseVo functionOpenBaseVo = Model.FunctionOpenBaseMap.get(ModuleUIEnum.REDNESS_PALACE);
            if(functionOpenBaseVo == null)return;
            if(functionOpenBaseVo.needLevel > gangUserVo.cacheUserVo.level)continue;
            if(mapBaseVo.needLevel > gangUserVo.cacheUserVo.level)continue;
           // long time = System.currentTimeMillis() - client1.lastRqstTime;
           // if(time>60000*5)continue;
           // users.add(UserVoAdapter.toSimpleUserPVo (gangUserVo.cacheUserVo));
        }
        new RP_GangmenberListRspd(client,users);
    }
}
