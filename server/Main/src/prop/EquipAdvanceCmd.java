package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.EquipAdvanceRqst;
import protocol.EquipAdvanceRspd;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.EquipAdvanceBaseVo;
import table.PropBaseVo;
import table.ReasonTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/2/18.
 */
public class EquipAdvanceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EquipAdvanceRqst rqst = (EquipAdvanceRqst) baseRqst;
        PropPVo mainProp=null;
        if(rqst.equipIndex>-1) {
            mainProp=user.propModel.getEquipByIndex(rqst.equipIndex);
        }else{
            mainProp=user.propModel.getPropInBag(rqst.bagTempID);
        }
        //stoneProp=  mainProp=user.propModel.getPropInBag(rqst.stoneTempID);
        if( mainProp==null)return;

        //是否能升级
        EquipAdvanceBaseVo advanceBaseVo = Model.EquipAdvanceBaseMap.get(mainProp.advance+1);
        if(advanceBaseVo == null)return;
        if(mainProp.intensify != advanceBaseVo.needlevel)return;
        PropBaseVo propBaseVo = Model.PropBaseMap.get(mainProp.baseID);
        if(propBaseVo == null || propBaseVo.quality <= 1)return;
        if(mainProp.advance > 0){//升1级不需要计算消耗 直接读表
            EquipAdvanceBaseVo lastOne = Model.EquipAdvanceBaseMap.get((int)mainProp.advance);
            if(lastOne == null)return;
            HashMap<Integer,Integer> allItems=new HashMap<>();
            for(int i=0;i<advanceBaseVo.costItems.length;i+=2){
                int baseID = advanceBaseVo.costItems[i];
                if(allItems.containsKey(baseID)){
                    allItems.put(baseID,allItems.get(baseID)+advanceBaseVo.costItems[i+1]);
                }else{
                    allItems.put(baseID,advanceBaseVo.costItems[i+1]);
                }
            }
            for(int i=0;i<lastOne.costItems.length;i+=2){
                int baseID = lastOne.costItems[i];
                if(allItems.containsKey(baseID)){
                    int count = allItems.get(baseID)-lastOne.costItems[i+1];
                    if(count > 0){
                        allItems.put(baseID,count);
                    }else if(count == 0){
                        allItems.remove(baseID);
                    }else{
                        return;
                    }
                }else{
                    return;
                }
            }
            int[] arr = new int[2*allItems.size()];
            int i = 0;
            for(Map.Entry<Integer, Integer>  item:allItems.entrySet()){
                arr[i++] = item.getKey();
                arr[i++] = item.getValue();
            }
            //消耗
            if (user.costUserDataAndPropList(arr, true, ReasonTypeEnum.EQUIP_ADVANCE,null) == false)return;
        }else{
            //消耗
            if (user.costUserDataAndPropList(advanceBaseVo.costItems, true,ReasonTypeEnum.EQUIP_ADVANCE,null) == false)return;
        }

        //升级
        mainProp.advance++;

        //保存与计算战斗力
        AllSql.propSql.update(AllSql.propSql.FIELD_ADVANCE, mainProp.advance + "", mainProp.tempID);
        new EquipAdvanceRspd(client,rqst.equipIndex,rqst.bagTempID,mainProp.advance);
        if(rqst.equipIndex>-1) {
            user.updateZDL();
        }
    }
}
