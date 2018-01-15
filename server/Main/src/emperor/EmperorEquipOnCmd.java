package emperor;

import com.sun.org.apache.regexp.internal.RE;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.PropBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class EmperorEquipOnCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorEquipOnRqst rqst = (EmperorEquipOnRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.emperorID);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        PropBaseVo propBaseVo = Model.PropBaseMap.get(rqst.propID);
        if(propBaseVo == null)return;
        if(EmperorModel.isEmperorEquip(propBaseVo) == false)return;
        ArrayList<Integer> list = (ArrayList)emperorPVo.equip;
        int index = propBaseVo.ID%10-1;
        if(index >= list.size())return;
        int oldProp = list.get(index);
        if(oldProp == propBaseVo.ID)return;
        if(user.costUserDataAndProp(propBaseVo.ID,1,true, ReasonTypeEnum.EMPEROR) == false)return;
        list.set(index,propBaseVo.ID);
        if(oldProp != 0){
            user.propModel.addListToBag(new int[]{oldProp,1},ReasonTypeEnum.EMPEROR);
        }
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
