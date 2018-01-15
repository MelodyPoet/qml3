package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DeletePropRspd;
import protocol.FusionEquipRqst;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.*;

import java.util.HashMap;
import java.util.Map;


public class FusionEquipCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        FusionEquipRqst rqst = (FusionEquipRqst) baseRqst;

        PropPVo propPVo = user.propModel.getPropInBag(rqst.tempID);
        if (propPVo == null)return;

        HashMap<Integer,Integer> map = new HashMap<>();
        EquipIntensifyUpBaseVo upBaseVo = Model.EquipIntensifyUpBaseMap.get((int) propPVo.intensify);
        if (upBaseVo == null) return;
        EquipAdvanceBaseVo equipAdvanceBaseVo = Model.EquipAdvanceBaseMap.get((int) propPVo.advance);
        int count = 0;
        if(propPVo.advance >0){
            //消耗
//            if (user.costUserDataAndPropList(equipAdvanceBaseVo.fusionCost, true, null) == false) return;

            //强化石数量
            PropBaseVo baseVo = Model.PropBaseMap.get(propPVo.baseID);
            if (baseVo == null) return;
            EquipIntensifyExpBaseVo expBaseVo = Model.EquipIntensifyExpBaseMap.get(baseVo.level);
            if (expBaseVo == null) return;
            int exp = expBaseVo.exp[baseVo.quality] + propPVo.exp;
            PropBaseVo stone = Model.PropBaseMap.get(upBaseVo.intensifyStone);
            if (stone == null) return;
            count += exp * Model.GameSetBaseMap.get(23).intValue / 100 / stone.effect[0];

            //熔炼产出
            for (int i = 0; i < equipAdvanceBaseVo.costItems.length; i += 2) {
                int id = equipAdvanceBaseVo.costItems[i];
                int num = equipAdvanceBaseVo.costItems[i+1];
                if(Model.PropComposeBaseMap.containsKey(id)){
                    PropComposeBaseVo vo = Model.PropComposeBaseMap.get(id);
                    if(vo.needProp == null){
                        addPropToMap(map,id,num);
                        continue;
                    }
                    for(int j=0;j<vo.needProp.length;j+=2){
                        addPropToMap(map,vo.needProp[j],vo.needProp[j+1]*num);
                    }
                }else{
                    addPropToMap(map,id,num);
                }
            }
        }

        HashMap<Integer,Integer> purifyCost = new HashMap<>();
        EquipPurifyBaseVo equipPurifyBaseVo = Model.EquipPurifyBaseMap.get((int)propPVo.purify);
        if(propPVo.purify > 0){
            purifyCost = equipPurifyBaseVo.costItems;
        }
        int[] arr = new int[map.size()*2 + purifyCost.size()*2 + 2];
        arr[0] = upBaseVo.intensifyStone;
        arr[1] = count;
        int index = 2;
        for(Map.Entry<Integer,Integer> item : map.entrySet()){
            arr[index++] = item.getKey();
            arr[index++] = item.getValue()*equipAdvanceBaseVo.costReturn/100;
        }
        for(Map.Entry<Integer,Integer> item : purifyCost.entrySet()){
            arr[index++] = item.getKey();
            arr[index++] = item.getValue()/100;
        }
        user.propModel.addListToBag(arr,ReasonTypeEnum.FUSION_EQUIP);
//        EquipDissolveBaseVo equipDissolveBaseVo = Model.EquipDissolveBaseMap.get(baseVo.level);
//       if(user.costUserDataAndPropList(equipDissolveBaseVo.fusionCost,true,null)==false)return;
//// 强化挨个返回 先统计
//        int money=0;
//        HashMap<Integer,Integer> allItems=new HashMap<>();
//        for (int i = 1; i <= propPVo.intensify+1; i++) {
//            EquipIntensifyUpBaseVo equipIntensifyUpBaseVo = Model.EquipIntensifyUpBaseMap.get(i);
//            if(equipIntensifyUpBaseVo.costItems == null)return;
//            for (int j = 0; j < equipIntensifyUpBaseVo.costItems.length ; j+=2) {
//                int baseID= equipIntensifyUpBaseVo.costItems[j];
//               if(baseID >100){
//                   if(allItems.containsKey(baseID)){
//                       allItems.put(baseID,allItems.get(baseID)+ equipIntensifyUpBaseVo.costItems[j+1]);
//                   }else{
//                       allItems.put(baseID, equipIntensifyUpBaseVo.costItems[j+1]);
//                   }
//               }else if(baseID== UserDataEnum.MONEY){
//                   money+= equipIntensifyUpBaseVo.costItems[j+1];
//               }
//            }
//
//        }
//        if(money>0)user.addUserData(UserDataEnum.MONEY,money,true);
//         for (  Map.Entry<Integer, Integer>  item:allItems.entrySet()){
//            user.propModel.addInBag(item.getKey(),item.getValue(),true,true);
//        }


        //删熔炼道具
        AllSql.propSql.delete(propPVo.tempID);
        user.propModel.deleteBag(propPVo.tempID,true,ReasonTypeEnum.FUSION_EQUIP);
        new DeletePropRspd(client, propPVo.tempID);
    }

    public void addPropToMap(HashMap<Integer,Integer> map,int id,int num){
        if(map.containsKey(id)){
            map.put(id,map.get(id)+num);
        }else{
            map.put(id,num);
        }
    }
}
