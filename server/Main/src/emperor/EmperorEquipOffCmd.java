package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class EmperorEquipOffCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorEquipOffRqst rqst = (EmperorEquipOffRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.emperorID);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        ArrayList<Integer> list = (ArrayList)emperorPVo.equip;
        if(rqst.index >= list.size())return;
        int oldProp = list.get(rqst.index);
        if(oldProp == 0)return;
        list.set(rqst.index,0);
        user.propModel.addListToBag(new int[]{oldProp,1}, ReasonTypeEnum.EMPEROR);
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
