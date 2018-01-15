package dragon;

import comm.*;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.AwardShowRspd;
import protocol.DragonTouchRqst;
import protocol.PropPVo;
import table.*;

import java.util.ArrayList;
import java.util.HashSet;


public class DragonTouchCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DragonTouchRqst rqst = (DragonTouchRqst) baseRqst;
        touchDragon(client, user, rqst.userID, rqst.tempID);
    }

    private void touchDragon(Client client, User user, long userID, byte tempID) {
        if (user.guid == userID) return;
        if (!CacheUserVo.allMap.containsKey(userID)) return;
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
        if (cacheUserVo == null) return;
        if (!cacheUserVo.dragonEggModel.touchMap.containsKey(tempID)) return;
        DragonEggVo eggVo = null;
        for(DragonEggVo dragonEggVo : cacheUserVo.dragonEggModel.dragonEggList){
            if(dragonEggVo.tempID == tempID){
                int time = JkTools.getGameServerTime(client) - dragonEggVo.time;
                if(time >= 0)return;
                if(dragonEggVo.count <= 0)return;
                eggVo = dragonEggVo;
                break;
            }
        }
        if(eggVo == null)return;
        HashSet<Long> set = cacheUserVo.dragonEggModel.touchMap.get(tempID);
        if (set.contains(userID)) return;
        if (user.costUserDataAndProp(UserDataEnum.CAN_TOUCH_COUNT, 1, true,ReasonTypeEnum.DRAGON_TOUCH) == false) return;
        set.add(user.guid);
        DragonEggBaseVo baseVo = Model.DragonEggBaseMap.get((int)eggVo.eggID);
        if(baseVo == null)return;
        eggVo.time -= baseVo.reduceTime;
        eggVo.count--;
        if(cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = cacheUserVo.gang.gangVo;
        for(GangUserVo gangUserVo : gangVo.users.values()){
            if(gangUserVo.cacheUserVo.guid == userID){
                if(!cacheUserVo.dragonEggModel.myEggUpSet.contains(tempID)){
                    cacheUserVo.dragonEggModel.myEggUpSet.add(tempID);
                }
            }else{
                if(gangUserVo.cacheUserVo.dragonEggModel.nowGangUserEggSet.contains(userID)){
                    gangUserVo.cacheUserVo.dragonEggModel.theirsEggUpSet.add(userID);
                }
            }
        }
        cacheUserVo.dragonEggModel.saveSqlData();
        CommAwardBaseVo awardBaseVo = Model.CommAwardBaseMap.get((int)ModuleAwardEnum.TouchEgg);
        if(awardBaseVo == null)return;
        user.propModel.addListToBag(awardBaseVo.awards,ReasonTypeEnum.DRAGON_TOUCH);
        ArrayList<PropPVo> awardList = new ArrayList<>();
        for(int i=0;i<awardBaseVo.awards.length;i+=2){
            PropPVo propPVo = new PropPVo();
            propPVo.baseID = awardBaseVo.awards[i];
            propPVo.count = awardBaseVo.awards[i+1];
            awardList.add(propPVo);
        }
        new AwardShowRspd(client,awardList);
//        //TODO 修改任务ID
//        MissionBaseVo missionBaseVo = Model.MissionBaseMap.get(43001);
//        if(missionBaseVo==null||user.activationModel.acceptedList.containsKey(missionBaseVo)==false)return;
        // rqst.tempUserID
//        user.activationModel.progressAdd(missionBaseVo);
//        if (missionBaseVo == null) return;
        user.addUserData(UserDataEnum.LJ_TOUCH_COUNT, 1, true);
    }
}
