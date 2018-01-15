package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.*;

import java.util.List;


public class DragonLevelUpCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
//        user.dragonModel.flushDragonList(client);
        DragonLevelUpRqst rqst = (DragonLevelUpRqst) baseRqst;
        DragonPVo targetVo=  user.cacheUserVo.dragonCacheModel.dragonsMap.get(rqst.baseID);
        if(targetVo == null || !targetVo.isActive)return;
        if(user.cacheUserVo.level <= targetVo.level)return;
        int maxLevel = Model.DragonLevelBaseMap.size()-1;
        if(targetVo.level >= maxLevel)return;
        int exp = 0;
        int[] cost = new int[rqst.cost.size()];
        for(int i=0;i<rqst.cost.size()/2;i+=2){
            cost[i] = ((List<Integer>)rqst.cost).get(i);
            cost[i+1] = ((List<Integer>)rqst.cost).get(i+1);
            PropBaseVo propBaseVo = Model.PropBaseMap.get(cost[i]);
            exp += propBaseVo.effect[0] * cost[i+1];
        }
        if(user.costUserDataAndPropList(cost,true,ReasonTypeEnum.DRAGON_LEVEL_UP,null) == false)return;
        targetVo.exp += exp;
        int level = -1;
        for(int i=targetVo.level;i<maxLevel;i++){
            DragonLevelBaseVo vo = Model.DragonLevelBaseMap.get(i+1);
            if(targetVo.exp >= vo.needExp){
                continue;
            }else{
                level = i;
                break;
            }

        }
        if(level == -1){
            level = maxLevel;
            DragonLevelBaseVo vo = Model.DragonLevelBaseMap.get(maxLevel-1);
            targetVo.exp = vo.needExp;
        }
//        if(user.dragonModel.dragonLimitTimeMap.containsKey(targetVo.baseID)){
//            DragonLimitTimePVo dragonLimitTimePVo = user.dragonModel.dragonLimitTimeMap.get(targetVo.baseID);
//            dragonLimitTimePVo.deadTime = JkTools.getGameServerTime(client) + dragonLimitTimePVo.limitTime;
//            user.dragonModel.saveSqlData();
//        }
        if(level > targetVo.level){
            int count = level - targetVo.level;
            user.setUserData(UserDataEnum.LJ_DRAGON_UP_COUNT,level,true);
            for(int i=0;i<count;i++){
                user.activationModel.progressBuyAct(MissionConditionEnum.DragonLevelUp,0);
            }
            if(level > user.getUserData(UserDataEnum.MAX_DRAGON_LEVEL)){
                user.setUserData(UserDataEnum.MAX_DRAGON_LEVEL,level,true);
            }
            user.updateZDL();
        }
        targetVo.level = (byte) level;
        new AddDragonRspd(client,targetVo);
        user.cacheUserVo.dragonCacheModel.saveSqlData();
    }

//    private static void dragonAiring(User user,int baseId,ArrayList<AiringMessagePVo> list){
//        //走马灯
//        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(2);
//        if(airingVo==null)return;
//        if(airingVo.isUse != 1)return;
//        DragonBaseVo vo = Model.DragonBaseMap.get(baseId).get(0);
//        if(vo==null)return;
//        if(vo.quality%airingVo.divisor != 0)return;
//        if(JkTools.compare(vo.quality,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
//        if(airingVo.conditionParams.length>=4&& JkTools.compare(vo.quality,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
//        AiringMessagePVo pVo = new AiringMessagePVo();
//        pVo.type = 1;
//        String quality = "";
//        switch (vo.quality){
//            case 0: quality = "[D1D1D1]普通"; break;
//            case 1: quality = "[57E157]稀有"; break;
//            case 2: quality = "[57BCE1]传说"; break;
//            case 3: quality = "[D248FF]史诗"; break;
//            case 4: quality = "[EC9141]远古"; break;
//            case 5: quality = "[FF4848]绝世"; break;
//        }
////        pVo.msg = "恭喜 "+user.cacheUserVo.passportVo.name+" 获得 "+quality+" 级"+ vo.name +" 。";
//        pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",quality).replace("{3}",vo.name+"[-]");
//        pVo.time = 1;
//        list.add(pVo);
//    }
}
