package realm;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DanUpRqst;
import protocol.DanUpRspd;
import table.RealmDanBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

public class DanUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DanUpRqst rqst = (DanUpRqst)baseRqst;
        RealmModel realmModel = user.realmModel;
        byte realm = rqst.realm;
        byte dan = rqst.dan;
        RealmVo realmVo = realmModel.realmMap.get(realm);
        if(realmVo == null)return;
        ArrayList<RealmDanBaseVo> list = Model.RealmDanBaseMap.get((int)realm);
        if(list == null)return;
        if(dan >= list.size())return;
        RealmDanBaseVo realmDanBaseVo = list.get(realmVo.dan);
        if(realmDanBaseVo == null)return;
        if(dan != realmVo.dan+1)return;
        if(realmVo.cyclone != realmDanBaseVo.needCyclone)return;
        if(user.getUserData(UserDataEnum.LEVEL) < realmDanBaseVo.needLevel)return;
        if(user.costUserDataAndPropList(realmDanBaseVo.cost,true, ReasonTypeEnum.REALM,null) == false)return;
        realmVo.dan++;
        realmVo.cyclone = 0;
        user.realmModel.saveSqlData();
        new DanUpRspd(client,realmVo.realm,realmVo.dan,realmVo.cyclone);
    }
}
