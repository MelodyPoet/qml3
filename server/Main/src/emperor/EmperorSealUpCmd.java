package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.EmperorQualityBaseVo;
import table.EmperorSealBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

public class EmperorSealUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorSealUpRqst rqst = (EmperorSealUpRqst)baseRqst;
        EmperorPVo emperorPVo = user.emperorModel.emperorMap.get(rqst.id);
        if(emperorPVo == null)return;
        if(emperorPVo.advance == 0)return;
        ArrayList<EmperorQualityBaseVo> qualityList = Model.EmperorQualityBaseMap.get((int)rqst.id);
        if(emperorPVo.quality+1 < qualityList.size())return;
        ArrayList<EmperorSealBaseVo> sealList = Model.EmperorSealBaseMap.get((int)rqst.id);
        if(sealList == null)return;
        EmperorSealBaseVo emperorSealBaseVo = sealList.get(emperorPVo.seal);
        if(emperorSealBaseVo == null)return;
        if(user.getUserData(UserDataEnum.LEVEL) < emperorSealBaseVo.needLv)return;
        if(emperorPVo.count < emperorSealBaseVo.needCount)return;
        emperorPVo.count -= emperorSealBaseVo.needCount;
        emperorPVo.seal++;
        user.emperorModel.saveSqlData();
        new AddEmperorRspd(client,emperorPVo);
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = user.emperorModel.attributeMap.get(emperorPVo.id);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
