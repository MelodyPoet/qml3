package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.IntensifyRqst;
import protocol.IntensifyRspd;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.*;

import java.util.List;


public class IntensifyCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		IntensifyRqst rqst=(IntensifyRqst) baseRqst;


      PropPVo mainProp=null;
        if(rqst.equipIndex>-1) {
            mainProp=user.propModel.getEquipByIndex(rqst.equipIndex);
        }else{
            mainProp=user.propModel.getPropInBag(rqst.bagTempID);
        }
        //stoneProp=  mainProp=user.propModel.getPropInBag(rqst.stoneTempID);
        if( mainProp==null)return;

        //最大能升到几级
        EquipAdvanceBaseVo advanceBaseVo = Model.EquipAdvanceBaseMap.get(mainProp.advance+1);
        if(advanceBaseVo == null)return;
        int maxLevel = Math.min(user.getUserData(UserDataEnum.LEVEL),advanceBaseVo.needlevel);
        if(mainProp.intensify >= maxLevel)return;

        //计算经验和消耗
        int[] costItem = new int[rqst.tempID.size()*2];
        int equipCount = 0;
        int exp = 0;
        int size = rqst.tempID.size();
        if(rqst.count.size() != size)return;
        if(size > 0) {
            int money = 0;
            for (int i = 0; i < size; i++) {
                PropPVo propPVo = user.propModel.getPropInBag(((List<Long>)rqst.tempID).get(i));
                if (propPVo == null) return;
                if (propPVo.advance > 0) return;
                PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
                if (propBaseVo == null) return;
                costItem[2*i] = propPVo.baseID;
                costItem[2*i+1] = ((List<Integer>)rqst.count).get(i);
                if (PropModel.isEquip(propPVo.baseID)) {
                    EquipIntensifyExpBaseVo expBaseVo = Model.EquipIntensifyExpBaseMap.get(propBaseVo.level);
                    if (expBaseVo == null) return;
                    money += expBaseVo.gold[propBaseVo.quality];
                    exp += expBaseVo.exp[propBaseVo.quality] + propPVo.exp;
                    equipCount += ((List<Integer>)rqst.count).get(i);
                } else {//强化石
                    exp += propBaseVo.effect[0]*costItem[2*i+1];
                }
            }

            if(money > user.getUserData(UserDataEnum.MONEY))return;
            if (user.costUserDataAndPropList(costItem, true,ReasonTypeEnum.INTENSIFY,null) == false)return;
            user.setUserData(UserDataEnum.MONEY,user.getUserData(UserDataEnum.MONEY) - money,true,ReasonTypeEnum.INTENSIFY);
        }

        //升级
        PropBaseVo mainBaseVo = Model.PropBaseMap.get(mainProp.baseID);
        if(mainBaseVo == null)return;
        mainProp.exp += exp;
        for(int i=mainProp.intensify;i<maxLevel;i++){
            EquipIntensifyUpBaseVo upBaseVo = Model.EquipIntensifyUpBaseMap.get(i+1);
            if(mainProp.exp >= upBaseVo.needExp[mainBaseVo.quality]){
                mainProp.intensify++;
                if(mainProp.intensify == maxLevel){
                    break;
                }
            }else {
                break;
            }
        }

        //强化功能修改
//        boolean isUp = false;
//        for (int i = 0,len=rqst.toMax?1000:1; i <len; i++) {
//
//         EquipIntensifyBaseVo equipIntensifyBaseVo = Model.EquipIntensifyBaseMap.get((int)mainProp.intensify+1);
//            if(equipIntensifyBaseVo == null)return;
//            if(mainProp.intensify > 0){
//                EquipIntensifyBaseVo nowBaseVo = Model.EquipIntensifyBaseMap.get((int)mainProp.intensify);
//                if(nowBaseVo.halfStar != equipIntensifyBaseVo.halfStar && i>0){
//                    isUp = true;
//                    break;
//                }
//            }
//
//if(mainProp.intensify>=user.getUserData(UserDataEnum.LEVEL))break;
//
//if(rqst.autoBuy==false || isUp == true) {
//    if (!user.costUserDataAndPropList(equipIntensifyBaseVo.costItems, false,stoneTempIDList)) {
//
//        break;
//    }
//}  else {
//     if(!user.costUserDataAndPropListAutoBuy(equipIntensifyBaseVo.costItems, false,stoneTempIDList)){
//        break;
//    }
//}
//
//            costed=true;
//
//    //    DCGameLog.getInstance().AddLog(GameLogType.COINLOST, new String[]{user.guid + "",UserDataEnum.MONEY + "", (money)  + "", user.getUserData(UserDataEnum.MONEY) + "", "intensify"});
//
//
//
//        int   rate = (int)(equipIntensifyBaseVo.baseRate* equipIntensifyBaseVo.qualityRateList[mainBase.quality]/100);
//
//
//
//	 boolean success=false;
//        int rnd=(int)(Math.random()*10000);
//        if(rnd<rate){
//            success=true;
//        }
//
//
//        if(success) {
//            mainProp.intensify++;
//        }
//            user.activationModel.progressBuyAct(MissionConditionEnum.Intensify,0);
//            user.addUserData(UserDataEnum.LJ_EQUIP_INTENSIFY_COUNT,1,true);
//        }

//if(costed) {
//
//    user.setUserData(UserDataEnum.MONEY,user.getUserData(UserDataEnum.MONEY),true);
//    user.setUserData(UserDataEnum.DIAMOND,user.getUserData(UserDataEnum.DIAMOND),true);
//for (long tempID : stoneTempIDList){
//    if(tempID<=0)continue;
//user.propModel.updateInBag(tempID,true,true);
//}
    AllSql.propSql.update(AllSql.propSql.FIELD_EXP, mainProp.exp + "", mainProp.tempID);
    AllSql.propSql.update(AllSql.propSql.FIELD_INTENSIFY, mainProp.intensify + "", mainProp.tempID);
        user.activationModel.progressBuyAct(MissionConditionEnum.Intensify,0);
    new IntensifyRspd(client,rqst.equipIndex,rqst.bagTempID, mainProp.intensify,mainProp.exp);
//}
        if(equipCount > 0){
            user.addUserData(UserDataEnum.LJ_EQUIP_INTENSIFY_COUNT,equipCount,true);
        }
        if(rqst.equipIndex>-1) {
            user.updateZDL();
        }
        PropBaseVo vo = Model.PropBaseMap.get(mainProp.baseID);
        if(vo == null)return;
        if(vo.type == PropTypeEnum.NECK || vo.type == PropTypeEnum.RING){
            if(mainProp.intensify > user.getUserData(UserDataEnum.MAX_JEWELRY_INTENSIFY_LEVEL)){
                user.setUserData(UserDataEnum.MAX_JEWELRY_INTENSIFY_LEVEL,mainProp.intensify,true);
            }
        }else{
            if(mainProp.intensify > user.getUserData(UserDataEnum.MAX_EQUIP_INTENSIFY_LEVEL)){
                user.setUserData(UserDataEnum.MAX_EQUIP_INTENSIFY_LEVEL,mainProp.intensify,true);
            }
        }
 	}



}
