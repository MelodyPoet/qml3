package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.EmperorBaseVo;
import table.EmperorQualityBaseVo;

import java.util.ArrayList;

public class EmperorAwakenCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorAwakenRqst rqst = (EmperorAwakenRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.id);
        if(emperorPVo == null)return;
        ArrayList<EmperorQualityBaseVo> list = Model.EmperorQualityBaseMap.get((int)rqst.id);
        if(list == null)return;
        if(list.size() <= emperorPVo.quality+1)return;
        if(emperorPVo.quality == 0){
            EmperorBaseVo emperorBaseVo = Model.EmperorBaseMap.get((int)emperorPVo.id);
            if(emperorBaseVo == null)return;
            EmperorQualityBaseVo vo = list.get(emperorBaseVo.initialQuality);
            if(emperorPVo.count < vo.count)return;
            user.emperorModel.activeEmperor(emperorPVo,vo.count);
        }else{
            EmperorQualityBaseVo vo = list.get(emperorPVo.quality);
            if(emperorPVo.count < vo.count)return;
            emperorPVo.count -= vo.count;
            emperorPVo.quality++;
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
