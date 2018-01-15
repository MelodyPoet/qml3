package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.EmperorLevelUpBaseVo;
import table.ReasonTypeEnum;

public class EmperorLevelUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorLevelUpRqst rqst = (EmperorLevelUpRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.id);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        EmperorLevelUpBaseVo vo = Model.EmperorLevelUpBaseMap.get(emperorPVo.up+1);
        if(vo == null)return;
        if(vo.needLevel != emperorPVo.level)return;
        if(user.costUserDataAndPropList(vo.cost,true, ReasonTypeEnum.EMPEROR,null) == false)return;
        emperorPVo.up++;
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
