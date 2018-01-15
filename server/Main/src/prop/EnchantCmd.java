package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;


public class EnchantCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
//        EnchantRqst rqst= (EnchantRqst) baseRqst;
//        PropPVo weapon = user.propModel.getEquipByIndex(PropTypeEnum.MAIN_WEAPON);
//        EnchantshopBaseVo shopVo= Model.EnchantshopBaseMap.get(weapon.baseID);
//       if(rqst.locks.size()>=3)return;
//        if(rqst.locks.size()==2&&shopVo.lockCost2==0)return;
//        if(rqst.locks.size()==1&&shopVo.lockCost1==0)return;
//
//        int diamondCost=0;
//        if(rqst.locks.size()==1){
//            diamondCost=shopVo.lockCost1;
//        }else{
//            diamondCost=shopVo.lockCost2;
//        }
//        if(user.getUserData(UserDataEnum.DIAMOND)<diamondCost){
//            new ServerTipRspd(client,(short)17,null);
//            return;
//        }
//        if(user.getUserData(UserDataEnum.MONEY)<shopVo.money){
//            new ServerTipRspd(client,(short)12,null);
//            return;
//        }
////        if(user.costUserDataAndProp(UserDataEnum.ENCHANT_STONE, shopVo.materialCount, true)==false){
////            new ServerTipRspd(client,(short)11,null);
////
////            return;
////        }
//        if(user.costUserDataAndProp(UserDataEnum.DIAMOND, diamondCost, true)==false){
//             return;
//        }
//        if(user.costUserDataAndProp(UserDataEnum.MONEY, shopVo.money, true)==false){
//            return;
//        }
//        int dataCount=3;
//        if(shopVo.lockCost2==0)dataCount=2;
//        if(shopVo.lockCost1==0)dataCount=1;
//        HashSet<Integer> hasSkillID=new HashSet<>();
//        for (int id :rqst.locks){
//            hasSkillID.add(id);
//        }
//        for (int i = 0; i <dataCount ; i++) {
//
//            if(weapon.enchant.size()<=i){
//                weapon.enchant.add((short)getRandomSkillID(hasSkillID));
//            }else if(rqst.locks.indexOf( ((Short)((ArrayList)weapon.enchant).get(i)).intValue())==-1){
//                ((ArrayList)weapon.enchant).set(i,(short)getRandomSkillID(hasSkillID));
//
//            }
//        }
//
//         new AddPropRspd(client,PropCellEnum.EQUIP ,weapon);
//      //  user.missionModel.progressBuyAct(MissionActEnum.ENCHANT);
//
//
//
//
//    }
//int getRandomSkillID( HashSet<Integer> hasSkillID){
//
//         int rate = new Random().nextInt(10000);
//        for (EnchantBaseVo vo :  Model.EnchantBaseMap.values()) {
//            rate -= vo.itemRate;
//            if (rate < 0) {
//                if(hasSkillID.contains(vo.ID)){
//                    return getRandomSkillID(hasSkillID);
//                }else {
//                    hasSkillID.add(vo.ID);
//                    return vo.ID;
//                }
//            }
//
//        }
//        return 0;
     }


}
