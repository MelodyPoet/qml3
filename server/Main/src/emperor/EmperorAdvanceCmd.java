package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.EmperorAdvanceBaseVo;

import java.util.ArrayList;

public class EmperorAdvanceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorAdvanceRqst rqst = (EmperorAdvanceRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.id);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        ArrayList<EmperorAdvanceBaseVo> list = Model.EmperorAdvanceBaseMap.get((int)rqst.id);
        if(list == null)return;
        if(list.size() <= emperorPVo.advance+1)return;
        if(emperorPVo.materials.size() < 6)return;
        emperorPVo.materials.clear();
        emperorPVo.advance++;
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
