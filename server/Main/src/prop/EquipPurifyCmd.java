package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import sqlCmd.AllSql;
import table.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/2/18.
 */
public class EquipPurifyCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EquipPurifyRqst rqst = (EquipPurifyRqst) baseRqst;
        PropPVo mainProp=null;
        if(rqst.equipIndex>-1) {
            mainProp=user.propModel.getEquipByIndex(rqst.equipIndex);
        }else{
            mainProp=user.propModel.getPropInBag(rqst.bagTempID);
        }
        if( mainProp==null)return;

        //是否能升级
        EquipPurifyBaseVo purifyBaseVo = Model.EquipPurifyBaseMap.get(mainProp.purify+1);
        if(purifyBaseVo == null)return;
        //消耗
        HashMap<Integer,Integer> allItems = new HashMap<>();
        if(mainProp.purify >0){
            EquipPurifyBaseVo lastOne = Model.EquipPurifyBaseMap.get((int)mainProp.purify);
            HashMap<Integer,Integer> lastCost = lastOne.costItems;
            for(Map.Entry<Integer,Integer> item : purifyBaseVo.costItems.entrySet()){
                int propID = item.getKey();
                if(lastCost.containsKey(propID)){
                    allItems.put(propID,item.getValue()-lastCost.get(propID));
                }else{
                    allItems.put(propID,item.getValue());
                }
            }
        }else{
             allItems = purifyBaseVo.costItems;
        }
        int[] arr = new int[2*allItems.size()];
        int i = 0;
        for(Map.Entry<Integer, Integer>  item:allItems.entrySet()){
            arr[i++] = item.getKey();
            arr[i++] = item.getValue();
        }
        if (user.costUserDataAndPropList(arr, true, ReasonTypeEnum.EQUIP_PURIFY, null) == false) return;

        //升级
        mainProp.purify++;

        //保存与计算战斗力
        AllSql.propSql.update(AllSql.propSql.FIELD_PURIFY, mainProp.purify + "", mainProp.tempID);
        user.activationModel.progressBuyAct(MissionConditionEnum.PROP_PURIFY,0);
        new EquipPurifyRspd(client,rqst.equipIndex,rqst.bagTempID,mainProp.purify);
        if(rqst.equipIndex>-1) {
            user.updateZDL();
        }
    }
}
