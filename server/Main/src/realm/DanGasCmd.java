package realm;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DanGasRqst;
import protocol.DanPVo;
import protocol.DanRspd;
import table.RealmDanBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class DanGasCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DanGasRqst rqst = (DanGasRqst)baseRqst;
        RealmModel realmModel = user.realmModel;
        byte realm = rqst.realm;
        byte dan = rqst.dan;
        RealmVo realmVo = realmModel.realmMap.get(realm);
        if(realmVo == null)return;
        if(dan > realmVo.dan)return;
        DanPVo danPVo = realmVo.danMap.get(dan);
        if(danPVo == null){
            danPVo = new DanPVo();
            danPVo.dan = dan;
            realmVo.danMap.put(dan,danPVo);
        }
        ArrayList<RealmDanBaseVo> list = Model.RealmDanBaseMap.get((int)realm);
        if(list == null)return;
        RealmDanBaseVo realmDanBaseVo = list.get(danPVo.gasStar);
        if(realmDanBaseVo == null)return;
        if(user.costUserDataAndPropList(realmDanBaseVo.cost,true, ReasonTypeEnum.REALM,null) == false)return;
        danPVo.gasStar++;
        user.realmModel.saveSqlData();
        new DanRspd(client,realm,danPVo);
    }
}
