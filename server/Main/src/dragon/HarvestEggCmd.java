package dragon;

import base.BaseModel;
import base.EggQualityPVoJoin;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import sqlCmd.AllSql;
import table.DragonEggBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/18.
 */
public class HarvestEggCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        HarvestEggRqst rqst = (HarvestEggRqst)baseRqst;
        harvestEgg(client,user,rqst.tempID);
    }

    private void harvestEgg(Client client,User user,byte tempID){
        DragonEggModel dragonEggModel = user.cacheUserVo.dragonEggModel;
        if(!dragonEggModel.touchMap.containsKey(tempID))return;
        DragonEggVo eggVo = dragonEggModel.getEggByTempID(tempID);
        if(eggVo == null)return;
        int time = JkTools.getGameServerTime(client) - eggVo.time;
        if(time<0)return;
        DragonEggBaseVo baseVo = Model.DragonEggBaseMap.get((int)eggVo.eggID);
        if(baseVo == null)return;
        ArrayList<PropPVo> dropList = BaseModel.getDropProps(baseVo.hatchDropID,user.baseID);
        if(dropList == null)return;
        user.propModel.addListToBag(dropList, ReasonTypeEnum.HATCH_CLEAR_CD);
        new AwardShowRspd(client,dropList);
        dragonEggModel.dragonEggList.remove(eggVo);
        dragonEggModel.touchMap.remove(eggVo.tempID);
        dragonEggModel.saveSqlData();
        user.addUserData(UserDataEnum.LJ_HATCH_DRAGON_STONE,1,true);
        if(user.eggQualityMap.containsKey(eggVo.eggID)){
            ByteIntPVo byteIntPVo = user.eggQualityMap.get(eggVo.eggID);
            byteIntPVo.value ++;
        }else{
            ByteIntPVo byteIntPVo = new ByteIntPVo();
            byteIntPVo.key = eggVo.eggID;
            byteIntPVo.value = 1;
            user.eggQualityMap.put(byteIntPVo.key,byteIntPVo);
        }
        AllSql.userSql.update(user,AllSql.userSql.FIELD_EGG_QUALITY,"'"+ EggQualityPVoJoin.instance.joinCollection(user.eggQualityMap.values()) +"'");
        new UpdateHarvestEggQualityRspd(user.client,user.eggQualityMap.values());
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
