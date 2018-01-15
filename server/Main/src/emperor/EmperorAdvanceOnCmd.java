package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.EmperorAdvanceBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class EmperorAdvanceOnCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorAdvanceOnRqst rqst = (EmperorAdvanceOnRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.emperorID);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        if(emperorPVo.materials.contains(rqst.propID))return;
        ArrayList<EmperorAdvanceBaseVo> list = Model.EmperorAdvanceBaseMap.get((int)rqst.emperorID);
        if(list == null)return;
        EmperorAdvanceBaseVo emperorAdvanceBaseVo = list.get(emperorPVo.advance);
        if(emperorAdvanceBaseVo == null)return;
        if(JkTools.indexOf(emperorAdvanceBaseVo.costUserdata,rqst.propID) < 0)return;
        if(user.costUserDataAndProp(rqst.propID,1,true, ReasonTypeEnum.EMPEROR) == false)return;
        emperorPVo.materials.add(rqst.propID);
        user.emperorModel.saveSqlData();
        new EmperorAdvanceOnRspd(client,rqst.emperorID,rqst.propID);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
