package realm;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.CycloneUpPVo;
import protocol.CycloneUpRqst;
import protocol.CycloneUpRspd;
import table.RealmCycloneUpBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

public class CycloneUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        CycloneUpRqst rqst = (CycloneUpRqst)baseRqst;
        RealmModel realmModel = user.realmModel;
        byte realm = rqst.realm;
        byte dan = rqst.dan;
        byte cyclone = rqst.cyclone;
        RealmVo realmVo = realmModel.realmMap.get(realm);
        if(realmVo == null)return;
        if(dan > realmVo.dan)return;
        if(dan != realmVo.cycloneUpDan)return;
        if(dan == realmVo.dan && cyclone > realmVo.cyclone)return;
        if(cyclone > RealmModel.MAX_CYCLONE)return;
        if(realmVo.cycloneUp.contains(cyclone))return;
        ArrayList<RealmCycloneUpBaseVo> list = Model.RealmCycloneUpBaseMap.get((int)realm);
        if(list == null)return;
        RealmCycloneUpBaseVo realmCycloneUpBaseVo = list.get((dan-1)*10+cyclone-1);
        if(realmCycloneUpBaseVo == null)return;
        if(realmCycloneUpBaseVo.dan != dan && realmCycloneUpBaseVo.cycloneUp != cyclone)return;
        if(user.getUserData(UserDataEnum.LEVEL) < realmCycloneUpBaseVo.needLevel)return;
        if(user.costUserDataAndPropList(realmCycloneUpBaseVo.cost,true, ReasonTypeEnum.REALM,null) == false)return;
        int rate = client.passportVo.vip == 0 ? realmCycloneUpBaseVo.rate : realmCycloneUpBaseVo.vipRate;
        boolean flag = JkTools.getResult(rate,1000);
        if(flag){
            realmVo.cycloneUp.add(cyclone);
        }
        CycloneUpPVo cycloneUpPVo = new CycloneUpPVo();
        cycloneUpPVo.dan = realmVo.cycloneUpDan;
        cycloneUpPVo.cycloneUp = realmVo.cycloneUp;
        new CycloneUpRspd(client,realm,cycloneUpPVo);
        if(realmVo.cycloneUp.size() >= RealmModel.MAX_CYCLONE){
            if(realmVo.cycloneUpDan < realmVo.dan){
                realmVo.cycloneUpDan++;
                realmVo.cycloneUp.clear();
            }
        }
        if(flag)realmModel.saveSqlData();
    }
}
