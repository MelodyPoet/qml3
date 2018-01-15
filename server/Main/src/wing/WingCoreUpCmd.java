package wing;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.WingCorePVo;
import protocol.WingCoreRspd;
import protocol.WingCoreUpRqst;
import table.PropBaseVo;
import table.ReasonTypeEnum;
import table.WingCoreUpBaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/27.
 */
public class WingCoreUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        WingCoreUpRqst rqst = (WingCoreUpRqst)baseRqst;
        WingModel wingModel = user.cacheUserVo.wingModel;
        WingCorePVo wingCorePVo = wingModel.coreMap.get(rqst.id);
        ArrayList<WingCoreUpBaseVo> list = Model.WingCoreUpBaseMap.get((int)rqst.id);
        if(list == null)return;
        if(wingCorePVo.level >= list.size())return;
        int count = rqst.costItem.size();
        int[] arr = new int[count];
        int exp = 0;
        for(int i=0;i<count;i+=2){
            arr[i] =((List<Integer>) rqst.costItem).get(i);
            PropBaseVo propBaseVo = Model.PropBaseMap.get(arr[i]);
            if(propBaseVo == null)return;
            arr[i+1] = ((List<Integer>)rqst.costItem).get(i+1);
            exp += propBaseVo.effect[0]*arr[i+1];
        }
        if(user.costUserDataAndPropList(arr,true, ReasonTypeEnum.WING_CORE_UP,null) == false)return;
        int maxAddLevel = list.size() - wingCorePVo.level;
        wingCorePVo.exp += exp;
        for(int i=0;i<maxAddLevel;i++){
            WingCoreUpBaseVo baseVo = list.get(wingCorePVo.level-1);
            if(wingCorePVo.exp < baseVo.needExp){
                break;
            }else{
                wingCorePVo.level++;
            }
        }
        if(wingCorePVo.level == list.size()-1){
            WingCoreUpBaseVo baseVo = list.get(wingCorePVo.level-1);
            if(wingCorePVo.exp > baseVo.needExp)wingCorePVo.exp = baseVo.needExp;
        }
        wingModel.saveSqlData();
        user.updateZDL();
        new WingCoreRspd(client,(byte)1,wingCorePVo);
    }
}
