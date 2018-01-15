package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DragonPVo;
import protocol.DragonTalentOnRqst;
import protocol.DragonTalentOnRspd;
import protocol.DragonTalentPVo;

import java.util.ArrayList;

/**
 * Created by admin on 2017/3/24.
 */
public class DragonTalentOnCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DragonTalentOnRqst rqst = (DragonTalentOnRqst) baseRqst;
        ArrayList<DragonTalentPVo> talent = user.dragonModel.dragonTalentMap.get(rqst.dragonID);
        if(talent == null)return;
        DragonPVo dragonPVo = user.cacheUserVo.dragonCacheModel.dragonsMap.get(rqst.dragonID);
        if(dragonPVo == null || !dragonPVo.isActive)return;
        int[] unlockCondition = Model.DragonBaseMap.get((int)rqst.dragonID).unlockCondition;
        int index = rqst.index*2;
        switch (unlockCondition[index]){
            case 1:
                if(dragonPVo.advance < unlockCondition[index+1])return;
                break;
            case 2:
                if(dragonPVo.quality < unlockCondition[index+1])return;
                break;
            case 3:
                return;
        }
        DragonTalentPVo oldDragonTalent = null;
        DragonTalentPVo newDragonTalent = null;
        for(DragonTalentPVo pVo : talent){
            if(pVo.index == rqst.index){
                oldDragonTalent = pVo;
            }
            if(pVo.talentID == rqst.talentID){
                newDragonTalent = pVo;
            }
        }
        if(newDragonTalent == null || (oldDragonTalent != null && newDragonTalent.talentID == oldDragonTalent.talentID))return;
        if(oldDragonTalent != null){
            oldDragonTalent.index = -1;
        }
        newDragonTalent.index = rqst.index;
        new DragonTalentOnRspd(client,rqst.dragonID,newDragonTalent);
        user.dragonModel.saveSqlData();
    }
}
