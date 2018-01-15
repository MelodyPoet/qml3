package realm;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.CycloneOpenRqst;
import protocol.CycloneOpenRspd;
import table.RealmBaseVo;
import table.RealmCycloneBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

public class CycloneOpenCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        CycloneOpenRqst rqst = (CycloneOpenRqst)baseRqst;
        RealmModel realmModel = user.realmModel;
        byte realm = rqst.realm;
        byte dan = rqst.dan;
        byte cyclone = rqst.cyclone;
        RealmVo realmVo = realmModel.realmMap.get(realm);
        if(cyclone > RealmModel.MAX_CYCLONE)return;
        if(realmVo == null){
            if(dan != 1 || cyclone != 1)return;
            RealmBaseVo realmBaseVo = Model.RealmBaseMap.get((int)realm);
            if(realmBaseVo == null)return;
            //TODO 能否开启新阶级
            realmVo = new RealmVo();
        }else{
            if(realm != realmVo.realm)return;
            if(dan != realmVo.dan)return;
            if(cyclone != realmVo.cyclone+1)return;
        }
        ArrayList<RealmCycloneBaseVo> list = Model.RealmCycloneBaseMap.get((int)realm);
        if(list == null)return;
        RealmCycloneBaseVo realmCycloneBaseVo = list.get((dan-1)*10+cyclone-1);
        if(realmCycloneBaseVo == null)return;
        if(realmCycloneBaseVo.dan != dan || realmCycloneBaseVo.cyclone != cyclone)return;
        if(user.getUserData(UserDataEnum.LEVEL) < realmCycloneBaseVo.needLevel)return;
        if(user.costUserDataAndPropList(realmCycloneBaseVo.cost,true, ReasonTypeEnum.REALM,null) == false)return;
        int rate = client.passportVo.vip == 0 ? realmCycloneBaseVo.rate : realmCycloneBaseVo.vipRate;
        if(JkTools.getResult(rate,100)){
            realmVo.cyclone++;
            if(realmVo.cyclone == 1 && realmVo.dan == 1){
                realmModel.realmMap.put(realm,realmVo);
            }
            realmModel.saveSqlData();
        }
        new CycloneOpenRspd(client,realm,realmVo.dan,realmVo.cyclone);
    }
}
