package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ClearCDRqst;
import protocol.ClearCDRspd;
import protocol.MapEnterTimesRspd;
import protocol.MapEnteredPVo;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;


public class ClearCDCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        ClearCDRqst rqst = (ClearCDRqst) baseRqst;

        CommCooldownBaseVo cooldownBaseVo= Model.CommCooldownBaseMap.get((int)rqst.type);
        if(cooldownBaseVo.clearCost<=0)return;

        if(rqst.type == UserDataEnum.NEXTTIME_BAG_CELL){
           clearBagCellCD(client,user,cooldownBaseVo);
        }

        if(rqst.type == UserDataEnum.CD_COMPOSE_CLIPS){
            clearComposeClips(client,user,cooldownBaseVo);
        }

        if(rqst.type == UserDataEnum.WormNestNextEndTime){
            clearWormNestCD(client,user,cooldownBaseVo);
        }

        if(rqst.type == UserDataEnum.Talent_CD){
            clearTalentCD(client,user,cooldownBaseVo);
        }

        if(rqst.type == 10002){//地图挑战CD
            clearMapCD(client,user,cooldownBaseVo,(short) rqst.extra);
        }
        new ClearCDRspd(client,rqst.type);
    }

	 private void clearBagCellCD(Client client,User user, CommCooldownBaseVo cooldownBaseVo){
         user.flushBagCellCD(user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT));
         int time= JkTools.getGameServerTime(client)-user.getUserData(UserDataEnum.NEXTTIME_BAG_CELL);
         UserUpdateBaseVo extra = Model.UserUpdateBaseMap.get(13);
         if(extra==null){
             return;
         }
         if(user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)>=extra.max){
             return;
         }
         if(user.getUserData(UserDataEnum.BAG_CELL_CD_COUNT)-user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)>0){
             return;
         }
         int bagCDCellIndex = user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)+1;
         if(time<=0){
             BagBaseVo bagBaseVo = Model.BagBaseMap.get(bagCDCellIndex);
             if (user.costUserDataAndProp(UserDataEnum.DIAMOND, -time / cooldownBaseVo.clearCost + 1 + bagBaseVo.costItems[1], true,ReasonTypeEnum.BAG_CLEAR_CD) == false)
                 return;
         }
         BagBaseVo bagBaseVo = Model.BagBaseMap.get(bagCDCellIndex+1);
         if(bagBaseVo==null){
             if(bagCDCellIndex == extra.max){
                 user.setUserData(UserDataEnum.BAG_CELL_HAVE_COUNT,bagCDCellIndex,true);
                 user.setUserData(UserDataEnum.BAG_CELL_CD_COUNT,bagCDCellIndex,true);
                 user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL, -1, true);
                 user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME, -1, true);
             }
             return;
         }
         user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL,JkTools.getGameServerTime(client)+bagBaseVo.coolTime,true);
         user.addUserData(UserDataEnum.BAG_CELL_HAVE_COUNT,1,true);
         user.addUserData(UserDataEnum.BAG_CELL_CD_COUNT,1,true);
         user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME,JkTools.getGameServerTime(client),true);
         user.updateZDL();
         return;
     }


    private void clearComposeClips(Client client,User user, CommCooldownBaseVo cooldownBaseVo){
        int time= JkTools.getGameServerTime(client)-user.getUserData(UserDataEnum.CD_COMPOSE_CLIPS);
        if(time>3){
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, time / cooldownBaseVo.clearCost + 1, true,ReasonTypeEnum.CLIP_CLEAR_CD) == false)
                return;


        }
        user.setUserData(UserDataEnum.CD_COMPOSE_CLIPS,0);
    }

    private void clearWormNestCD(Client client,User user, CommCooldownBaseVo cooldownBaseVo){
        int time= JkTools.getGameServerTime(client)-user.getUserData(UserDataEnum.WormNestNextEndTime);
        if(time<=0){
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, -time / cooldownBaseVo.clearCost + 1, true,ReasonTypeEnum.WORM_NEST_CLEAR_CD) == false)
                return;
        }
        user.setUserData(UserDataEnum.WormNestNextEndTime,-1,true);
    }

    private void clearTalentCD(Client client,User user, CommCooldownBaseVo cooldownBaseVo){
        int time= JkTools.getGameServerTime(client)-user.getUserData(UserDataEnum.Talent_CD);
        if(time<=0){
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, -time / cooldownBaseVo.clearCost + 1, true,ReasonTypeEnum.TALENT_CLEAR_CD) == false)
                return;
        }
        user.setUserData(UserDataEnum.Talent_CD,-1,true);
    }

    private void clearMapCD(Client client,User user, CommCooldownBaseVo cooldownBaseVo,short mapID){
        if(!user.mapEnteredMap.containsKey(mapID))return;
        MapEnteredPVo mapEnteredPVo = user.mapEnteredMap.get(mapID);
        if(mapEnteredPVo.time == 0)return;
        int time= JkTools.getGameServerTime(client)-mapEnteredPVo.time;
        if(time<=0){
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, -time / cooldownBaseVo.clearCost + 1, true,ReasonTypeEnum.MAP_CLEAR_CD) == false)
                return;
        }
        mapEnteredPVo.time = 0;
        ArrayList<MapEnteredPVo> list= new ArrayList<>();
        list.add(mapEnteredPVo);
        new MapEnterTimesRspd(client,list);
        AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
    }
}
