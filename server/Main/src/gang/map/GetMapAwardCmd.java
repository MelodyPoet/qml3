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
import table.ReasonTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/4/28.
 */
public class GetMapAwardCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GetMapAwardRqst rqst = (GetMapAwardRqst)baseRqst;
        int count = 0;
        int now = JkTools.getGameServerTime(client);
        int[] dragonWish = gangVo.mapModel.dragonWish;
       ArrayList<GangMapAwardInfoPVo> awardList = new ArrayList<>();
        ArrayList<GangMapAwardPVo> list = new ArrayList<>();
        if(rqst.baseID == 0){
            for(HashMap<Long, GangMapAwardPVo> map : gangVo.mapModel.userMapAward.values()){
                if(map.containsKey(user.guid)){
                    GangMapAwardPVo gangMapAwardPVo = map.get(user.guid);
                    GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get((int)gangMapAwardPVo.baseID);
                    int time = now - gangMapAwardPVo.nextTime;
                    if(time < 0){
                        count = gangMapBaseVo.limit*dragonWish[1]/100 - (-time/gangMapBaseVo.coolTime*dragonWish[3]/100+1)*gangMapBaseVo.count*dragonWish[2]/100;
                    }else{
                        count = gangMapBaseVo.limit*dragonWish[1]/100;
                    }
                    gangMapAwardPVo.nextTime = now+gangMapBaseVo.limit*dragonWish[1]/100/gangMapBaseVo.count*dragonWish[2]/100*gangMapBaseVo.coolTime*dragonWish[3]/100;
                    gangMapAwardPVo.num = 0;
                    list.add(gangMapAwardPVo);
                    if(count > 0){
                        GangMapAwardInfoPVo pVo = new GangMapAwardInfoPVo();
                        pVo.baseID = gangMapAwardPVo.baseID;
                        pVo.propId = gangMapBaseVo.award;
                        pVo.num = count;
                        awardList.add(pVo);
                    }
                }
            }
        }else{
            if(gangVo.mapModel.userMapAward.containsKey(rqst.baseID)){
                HashMap<Long, GangMapAwardPVo> map = gangVo.mapModel.userMapAward.get(rqst.baseID);
                if(map.containsKey(user.guid)){
                    GangMapAwardPVo gangMapAwardPVo = map.get(user.guid);
                    GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get((int)gangMapAwardPVo.baseID);
                    int time = now - gangMapAwardPVo.nextTime;
                    if(time < 0){
                        count = gangMapBaseVo.limit*dragonWish[1]/100 - (-time/gangMapBaseVo.coolTime*dragonWish[3]/100+1)*gangMapBaseVo.count*dragonWish[2]/100;
                    }else{
                        count = gangMapBaseVo.limit*dragonWish[1]/100;
                    }
                    gangMapAwardPVo.nextTime = now+gangMapBaseVo.limit*dragonWish[1]/100/gangMapBaseVo.count*dragonWish[2]/100*gangMapBaseVo.coolTime*dragonWish[3]/100;
                    gangMapAwardPVo.num = 0;
                    list.add(gangMapAwardPVo);
                    if(count > 0){
                        GangMapAwardInfoPVo pVo = new GangMapAwardInfoPVo();
                        pVo.baseID = gangMapAwardPVo.baseID;
                        pVo.propId = gangMapBaseVo.award;
                        pVo.num = count;
                        awardList.add(pVo);
                    }
                }
            }else{
                return;
            }
        }
        ArrayList<PropPVo> propList = new ArrayList<>();
        int i=0;
        for(GangMapAwardInfoPVo pVo : awardList){
            PropPVo propPVo = new PropPVo();
            propPVo.baseID = pVo.propId;
            propPVo.count = pVo.num;
            propList.add(propPVo);
        }
        user.propModel.addListToBag(propList, ReasonTypeEnum.GANG_MAP);
        new GetMapAwardRspd(client,awardList);
        new GangMapAwardRspd(client,list);
        gangVo.saveGangMap();
    }
}
