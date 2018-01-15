package gang.map;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.ByteIntPVo;
import protocol.GangDragonRspd;
import protocol.WishRqst;
import protocol.WishRspd;
import table.*;

/**
 * Created by admin on 2017/5/2.
 */
public class WishCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WishRqst rqst = (WishRqst)baseRqst;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GangUserVo gangUserVo = gangVo.users.get(user.guid);
        if(gangUserVo == null)return;
        ByteIntPVo dragon = gangVo.mapModel.gangDragon;
        GangDragonUpBaseVo gangDragonUpBaseVo = Model.GangDragonUpBaseMap.get((int)dragon.key);
        int max = Model.GangDragonUpBaseMap.size()-1;
        if(dragon.key == max){
            GangDragonUpBaseVo MaxVo = Model.GangDragonUpBaseMap.get(max+1);
            if(dragon.value >= MaxVo.needExp)return;
        }
        int count = 0;
        int[] arr = Model.GameSetBaseMap.get(44).intArray;
        for(int i=0;i<rqst.count;){
            if(user.costUserDataAndPropList(gangDragonUpBaseVo.cost,true,ReasonTypeEnum.WISH,null) == false)break;
            dragon.value += gangDragonUpBaseVo.cost[1]*arr[1]/arr[0];
            count = ++i;
            user.activationModel.progressBuyAct(MissionConditionEnum.GANG_DRAGON_WISH,0);
            for(int level=dragon.key;level<Model.GangDragonUpBaseMap.size();level++){
                GangDragonUpBaseVo nextVo = Model.GangDragonUpBaseMap.get(level+1);
                if(dragon.value < nextVo.needExp)break;
                if(dragon.key == max){
                    if(dragon.value >= nextVo.needExp){
                        dragon.value = nextVo.needExp;
                    }
                }else{
                    dragon.key++;
                    for(int j=0;j<Model.GangDragonWishBaseMap.size();j++){
                        GangDragonWishBaseVo vo = Model.GangDragonWishBaseMap.get(j+1);
                        if(gangVo.mapModel.dragonWish[vo.ID] != 100)continue;
                        if(vo.needLevel > dragon.key)continue;
                        gangVo.mapModel.dragonWish[vo.ID] += vo.effct;
                        gangVo.mapModel.wish(vo.ID);
                    }
                    break;

                }
            }
        }
        if(count == 0)return;
        gangUserVo.setGangUserData(GangUserDataEnum.DRAGON_JINPO,gangUserVo.getGangUserData(GangUserDataEnum.DRAGON_JINPO),true);
        new WishRspd(client,(byte) count);
        new GangDragonRspd(client,dragon.value,dragon.key);
        gangVo.saveGangMap();
    }
}
