package prop;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AddPropRspd;
import protocol.DeletePropRspd;
import protocol.DissolveEquipRqst;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class DissolveEquipCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        DissolveEquipRqst rqst = (DissolveEquipRqst) baseRqst;
        HashMap<Integer,Integer> advanceReturn = new HashMap<>();
        HashMap<Integer,Integer> purifyReturn = new HashMap<>();
        HashMap<Integer,Integer> intensifyReturn = new HashMap<>();
        HashSet<Long> set = new HashSet<>();
        for (long tempID : rqst.tempList){
            PropPVo propPVo = user.propModel.getPropInBag(tempID);
        if (propPVo == null)return;

            if(propPVo.advance >0 || propPVo.purify >0){
                EquipAdvanceBaseVo equipAdvanceBaseVo = Model.EquipAdvanceBaseMap.get((int) propPVo.advance);
                if(propPVo.advance >0){
                    //强化石数量
                    PropBaseVo baseVo = Model.PropBaseMap.get(propPVo.baseID);
                    if (baseVo == null) return;
                    EquipIntensifyExpBaseVo expBaseVo = Model.EquipIntensifyExpBaseMap.get(baseVo.level);
                    if (expBaseVo == null) return;
                    int exp = expBaseVo.exp[baseVo.quality] + propPVo.exp;
                    EquipIntensifyUpBaseVo upBaseVo = Model.EquipIntensifyUpBaseMap.get((int) propPVo.intensify);
                    if (upBaseVo == null) return;
                    PropBaseVo stone = Model.PropBaseMap.get(upBaseVo.intensifyStone);
                    if (stone == null) return;
                    int count = exp * Model.GameSetBaseMap.get(23).intValue / 100 / stone.effect[0];
                    addPropToMap(intensifyReturn,upBaseVo.intensifyStone,count);

                    //熔炼产出
                    HashMap<Integer,Integer> map = new HashMap<>();
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
                    for(Map.Entry<Integer,Integer> item : map.entrySet()){
                        int num = item.getValue()*equipAdvanceBaseVo.costReturn/100;
                        if(num<=0)continue;
                        addPropToMap(advanceReturn,item.getKey(),num);
                    }
                }

                EquipPurifyBaseVo equipPurifyBaseVo = Model.EquipPurifyBaseMap.get((int)propPVo.purify);
                if(propPVo.purify > 0){
                    for(Map.Entry<Integer,Integer> item : equipPurifyBaseVo.costItems.entrySet()){
                        addPropToMap(purifyReturn,item.getKey(),item.getValue());
                    }
                }
                set.add(propPVo.tempID);
            }else{
                PropBaseVo baseVo = Model.PropBaseMap.get(propPVo.baseID);
                EquipDissolveBaseVo equipDissolveBaseVo = Model.EquipDissolveBaseMap.get(baseVo.level);
                int count = equipDissolveBaseVo.qualityCount[baseVo.quality];
                if (count > 0) {
                    PropPVo pVo = new PropPVo();
                    pVo.baseID = equipDissolveBaseVo.products;
                    pVo.count = count;
                    pVo= user.propModel.addInBag(pVo, true,true,ReasonTypeEnum.DISSOLVE_EQUIP);
                    new AddPropRspd(client, PropCellEnum.BAG,pVo);
                }

                new SellPropCmd(propPVo, baseVo).executeFinal(client, user);
            }
    }
        for(long tempID : set){
            //删熔炼道具
            AllSql.propSql.delete(tempID);
            user.propModel.deleteBag(tempID,true,ReasonTypeEnum.DISSOLVE_EQUIP);
            new DeletePropRspd(client,tempID);
        }
        int[] arr = new int[advanceReturn.size()*2 + purifyReturn.size()*2+intensifyReturn.size()*2];
        int index = 0;
        for(Map.Entry<Integer,Integer> item : intensifyReturn.entrySet()){
            arr[index++] = item.getKey();
            arr[index++] = item.getValue();
        }
        for(Map.Entry<Integer,Integer> item : advanceReturn.entrySet()){
            arr[index++] = item.getKey();
            arr[index++] = item.getValue();
        }
        for(Map.Entry<Integer,Integer> item : purifyReturn.entrySet()){
            arr[index++] = item.getKey();
            arr[index++] = item.getValue();
        }
        user.propModel.addListToBag(arr,ReasonTypeEnum.DISSOLVE_EQUIP);

        user.activationModel.progressBuyAct(MissionConditionEnum.EquipDissolve,0);
        user.addUserData(UserDataEnum.LJ_DISSOLVE_EQUIP_COUNT,rqst.tempList.size(),true);


	}

    public void addPropToMap(HashMap<Integer,Integer> map,int id,int num){
        if(map.containsKey(id)){
            map.put(id,map.get(id)+num);
        }else{
            map.put(id,num);
        }
    }

}
