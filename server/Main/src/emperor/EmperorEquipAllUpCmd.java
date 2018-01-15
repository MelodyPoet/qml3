package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.EmperorEquipAllUpRspd;
import protocol.PropPVo;
import table.PropBaseVo;
import table.PropComposeBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EmperorEquipAllUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        HashMap<Integer,Integer> map = new HashMap<>();
        for(PropPVo propPVo : user.propModel.bagItems.values()){
            PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
            if(EmperorModel.isEmperorEquip(propBaseVo) == false)continue;
            map.put(propBaseVo.effect[0],propBaseVo.ID);
        }
        ArrayList<Integer> list = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        for(int key : map.keySet()){
            dealMap(key,map,list,set);
        }

        HashSet<Long> stoneTempIDList = new HashSet<>();
        ArrayList<PropPVo> newPropList = new ArrayList<>();
        for(int propID : list){
            PropComposeBaseVo propComposeBaseVo = Model.PropComposeBaseMap.get(propID);
            if(propComposeBaseVo == null)continue;
            if (propComposeBaseVo.costdata == null && propComposeBaseVo.needProp == null) continue;
            int count = 0;
            for(int i=0;i<1000;i++){
                if (user.enoughForcostUserDataAndProp(propComposeBaseVo.costdata) == false) break;
                if (user.costUserDataAndPropList(propComposeBaseVo.needProp, false, ReasonTypeEnum.EMPEROR, stoneTempIDList) == false) break;
                if (user.costUserDataAndPropList(propComposeBaseVo.costdata, true, ReasonTypeEnum.EMPEROR, null) == false) break;
                count++;
            }
            if(count == 0)continue;
            PropPVo newPvo=new PropPVo();
            newPvo.baseID= propID;
            newPvo.count=count;
            newPropList.add(newPvo);
        }
        for (long tempID : stoneTempIDList) {
            if (tempID <= 0) continue;
            user.propModel.updateInBag(tempID, true, true);
        }
        user.propModel.addListToBag(newPropList,ReasonTypeEnum.EMPEROR);
        new EmperorEquipAllUpRspd(client);
    }

    private void dealMap(int key,HashMap<Integer,Integer> map,ArrayList<Integer> list,HashSet<Integer> set){
        int value = map.get(key);
        if(map.containsKey(value) && !set.contains(value)){
            dealMap(value,map,list,set);
        }else{
            list.add(key);
            set.add(key);
        }
    }
}
