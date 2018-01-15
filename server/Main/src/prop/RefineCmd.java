package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.PropPVo;
import protocol.RefineRqst;
import protocol.RefineRspd;
import table.*;

import java.util.ArrayList;
import java.util.List;


public class RefineCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RefineRqst rqst = (RefineRqst) baseRqst;



        PropPVo mainProp = null;
        byte cell= PropCellEnum.BAG;
        if (rqst.equipIndex > -1) {
            mainProp = user.propModel.getEquipByIndex(rqst.equipIndex);
            cell= PropCellEnum.EQUIP;
        } else {
            mainProp = user.propModel.getPropInBag(rqst.bagTempID);
        }
        if (mainProp == null) return;
        PropBaseVo mainBase = Model.PropBaseMap.get(mainProp.baseID);
//        if(user.propModel.refinedItem!=mainProp){
            user.propModel.refinedItem=mainProp;
            if(mainProp.exAttribute==null){
                user.propModel.refinedData=null;
            }else {
                user.propModel.refinedData=new ArrayList<>();
                for ( short item:  mainProp.exAttribute){
                    user.propModel.refinedData.add(item);
                }
            }
//        }


        EquipRefineBaseVo equipRefineBaseVo = Model.EquipRefineBaseMap.get(mainBase.level);
       int []totalCost = equipRefineBaseVo.refineCost;
        if(rqst.locks.size()>0) {
boolean lockError=false;
            if( user.propModel.refinedData==null){
                lockError=true;
            }
            if(lockError==false) {
                if (rqst.locks.size() >= mainBase.extrAttCountMax) {
                    lockError = true;
                }
            }
            if(lockError==false){
                for (byte index :rqst.locks){
                    if(index>=user.propModel.refinedData.size()||user.propModel.refinedData.get(index)==0){
                        lockError=true;
                    }
                }
            }
         if(lockError){
              return;
            // rqst.locks.clear();
         }else {

             byte lockCostType = (byte) equipRefineBaseVo.lockCost[(rqst.locks.size() - 1) * 2];
             int lockCostValue = equipRefineBaseVo.lockCost[(rqst.locks.size() - 1) * 2 + 1];
             if (user.enoughForcostUserDataAndProp(lockCostType, lockCostValue) == false) {
                 return;
             }
             totalCost = new int[equipRefineBaseVo.refineCost.length + 2];
             for (int i = 0; i < equipRefineBaseVo.refineCost.length; i++) {
                 totalCost[i] = equipRefineBaseVo.refineCost[i];
             }
             totalCost[equipRefineBaseVo.refineCost.length] = lockCostType;
             totalCost[equipRefineBaseVo.refineCost.length + 1] = lockCostValue;
         }

        }


        if (rqst.autoBuy == false) {
            if (!user.costUserDataAndPropList(totalCost, true,ReasonTypeEnum.REFINE, null)) {

                 return;
            }
        } else {
            if (!user.costUserDataAndPropListAutoBuy(totalCost, true,ReasonTypeEnum.REFINE, null)) {
                return;
            }
        }
        user.propModel.refinedData=user.propModel.createExtraAttributes(mainBase, (List<Byte>)rqst.locks);
//if(rqst.locks.size()>0){
//    //HashMap<Byte,Byte> old=new HashMap<>();
// //for (byte index :rqst.locks)
////old.put(index,items.get((int)index));
//
//    ArrayList<Byte> newitems=user.propModel.createExtraAttributes(mainBase,rqst.locks);
//
//    for (int index :rqst.locks)
//    {
//
//
////       if(mainBase.extrAttGroup.containsKey(index)){
////           for (int _i:mainBase.extrAttGroup.get(index)){
////               newitems.set(_i,(byte)0);
////           }
////       }
//        newitems.set(index, user.propModel.refinedData.get(index));
//
//    }
//    user.propModel.refinedData=newitems;
//
//}else {
//    user.propModel.refinedData=user.propModel.createExtraAttributes(mainBase,null);
//
//}
user.addUserData(UserDataEnum.LJ_REFINE_COUNT,1,true);
        user.activationModel.progressBuyAct(MissionConditionEnum.PROP_REFINE,0);
new RefineRspd(client,user.propModel.refinedData);
        //new RefineReplaceCmd().execute(client,user,null);
        if (rqst.equipIndex > -1) {
            user.updateZDL();
        }

    }


}
