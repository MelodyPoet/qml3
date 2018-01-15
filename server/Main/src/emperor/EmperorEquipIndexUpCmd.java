package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.PropBaseVo;
import table.PropComposeBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class EmperorEquipIndexUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorEquipIndexUpRqst rqst = (EmperorEquipIndexUpRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.emperorID);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        ArrayList<Integer> list = (ArrayList)emperorPVo.equip;
        if (rqst.index >= list.size()) return;
        int oldProp = list.get(rqst.index);
        if (oldProp == 0) return;
        PropBaseVo propBaseVo = Model.PropBaseMap.get(oldProp);
        if (propBaseVo == null) return;
        if (propBaseVo.effect == null) return;
        PropBaseVo vo = Model.PropBaseMap.get(propBaseVo.effect[0]);
        if (vo == null) return;
        if (propBaseVo.type != vo.type) return;
        PropComposeBaseVo propComposeBaseVo = Model.PropComposeBaseMap.get(vo.ID);
        if (propComposeBaseVo == null) return;
        if (propComposeBaseVo.costdata == null && propComposeBaseVo.needProp == null) return;
        if (user.enoughForcostUserDataAndProp(propComposeBaseVo.costdata) == false) return;
        int length = propComposeBaseVo.needProp.length;
        int[] cost = new int[length];
        for (int i = 0; i < length; i += 2) {
            cost[i] = propComposeBaseVo.needProp[i];
            if (cost[i] == oldProp) {
                cost[i + 1] = propComposeBaseVo.needProp[i + 1] - 1;
            }
        }
        if (user.costUserDataAndPropList(cost, true, ReasonTypeEnum.EMPEROR, null) == false) return;
        if (user.costUserDataAndPropList(propComposeBaseVo.costdata, true, ReasonTypeEnum.EMPEROR, null) == false) return;
        list.set(rqst.index, vo.ID);
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client, emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
