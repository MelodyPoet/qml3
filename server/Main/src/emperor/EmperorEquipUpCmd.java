package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.EmperorEquipUpRqst;
import protocol.PropPVo;
import table.PropBaseVo;
import table.PropComposeBaseVo;
import table.ReasonTypeEnum;

public class EmperorEquipUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorEquipUpRqst rqst = (EmperorEquipUpRqst)baseRqst;
        PropBaseVo propBaseVo = Model.PropBaseMap.get(rqst.propID);
        if(propBaseVo == null)return;
        if(propBaseVo.effect == null)return;
        PropBaseVo vo = Model.PropBaseMap.get(propBaseVo.effect[0]);
        if(vo == null)return;
        PropComposeBaseVo propComposeBaseVo = Model.PropComposeBaseMap.get(vo.ID);
        if(propComposeBaseVo == null)return;
        if (propComposeBaseVo.costdata == null && propComposeBaseVo.needProp == null) return;
        if (user.enoughForcostUserDataAndProp(propComposeBaseVo.costdata) == false) return;
        if (user.costUserDataAndPropList(propComposeBaseVo.needProp, true, ReasonTypeEnum.EMPEROR, null) == false) return;
        if (user.costUserDataAndPropList(propComposeBaseVo.costdata, true, ReasonTypeEnum.EMPEROR, null) == false) return;
        user.propModel.addListToBag(new int[]{vo.ID,1},ReasonTypeEnum.EMPEROR);
    }
}
