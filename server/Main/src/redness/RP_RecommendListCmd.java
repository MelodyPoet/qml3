package redness;

import comm.*;
import gluffy.comm.BaseRqst;
import protocol.RP_RecommendListRspd;
import protocol.SimpleUserPVo;
import table.FunctionOpenBaseVo;
import table.MapBaseVo;
import table.ModuleUIEnum;
import utils.UserVoAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/24.
 */
public class RP_RecommendListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ArrayList<SimpleUserPVo > users=new ArrayList<>();
        MapBaseVo mapBaseVo = Model.MapBaseMap.get(user.rednessModel.myRoom.mapID);
        for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
            if(users.size() > Model.GameSetBaseMap.get(27).intArray[0])continue;
            FunctionOpenBaseVo functionOpenBaseVo = Model.FunctionOpenBaseMap.get((int)ModuleUIEnum.REDNESS_PALACE);
            if(functionOpenBaseVo == null)return;
            if(functionOpenBaseVo.needLevel > cacheUserVo.level)continue;
            if(mapBaseVo.needLevel > cacheUserVo.level)continue;
            Client client1 = Client.getOne(cacheUserVo);
            //long time = System.currentTimeMillis() - client1.lastRqstTime;
           // if(time>60000*5)continue;
            users.add(UserVoAdapter.toSimpleUserPVo(cacheUserVo));
        }
        new RP_RecommendListRspd(client,users);
    }
}
