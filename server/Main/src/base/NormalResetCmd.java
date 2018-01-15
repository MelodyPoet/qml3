package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.NormalResetRqst;
import protocol.NormalResetRspd;
import protocol.MapEnteredPVo;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/3.
 */
public class NormalResetCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        NormalResetRqst rqst = (NormalResetRqst) baseRqst;
        resetNormal(client,user,rqst.mapID);
    }

    private void resetNormal(Client client,User user,int mapID){
        int count = user.getUserData(UserDataEnum.NORMAL_RESET_COUNT);
        if(count<=0)return;
        MapBaseVo vo = Model.MapBaseMap.get(mapID);
        if(vo == null)return;
        if(!user.mapEnteredMap.containsKey((short)mapID))return;
        MapEnteredPVo ssp = user.mapEnteredMap.get((short)mapID);
        if(ssp == null)return;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        int id = vipBaseVo.commMapReset - count;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get(1);
        if(costList == null)return;
        CommCostBaseVo costBaseVo = costList.get(id);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true, ReasonTypeEnum.NORMAL_RESET,null) == false)return;
        ssp.value = 0;
        AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
        user.setUserData(UserDataEnum.NORMAL_RESET_COUNT,count-1,true);
        new NormalResetRspd(client,mapID);
    }
}
