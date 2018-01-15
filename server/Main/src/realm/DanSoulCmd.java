package realm;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.DanPVo;
import protocol.DanRspd;
import protocol.DanSoulRqst;
import table.RealmDanSoulBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

public class DanSoulCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DanSoulRqst rqst = (DanSoulRqst)baseRqst;
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
        ArrayList<RealmDanSoulBaseVo> list = Model.RealmDanSoulBaseMap.get((int)realm);
        if(list == null)return;
        RealmDanSoulBaseVo realmDanSoulBaseVo = list.get(danPVo.soulStar);
        if(realmDanSoulBaseVo == null)return;
        if(user.costUserDataAndPropList(realmDanSoulBaseVo.cost,true, ReasonTypeEnum.REALM,null) == false)return;
        danPVo.soulStar++;
        user.realmModel.saveSqlData();
        new DanRspd(client,realm,danPVo);
    }
}
