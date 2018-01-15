package elit;

import base.MapEnteredPVoJoin;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/3.
 */
public class EliteBuyCountCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EliteBuyCountRqst rqst = (EliteBuyCountRqst) baseRqst;
        int mapID = rqst.mapID;
        int count = user.getUserData(UserDataEnum.ELITE_BUY_COUNT);
        if(count<=0)return;
        MapBaseVo vo = Model.MapBaseMap.get(mapID);
        if(vo == null)return;
        if(!user.mapEnteredMap.containsKey((short)mapID))return;
        MapEnteredPVo ssp = user.mapEnteredMap.get((short)mapID);
        if(ssp == null)return;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        int id = vipBaseVo.eliteByCount - count;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int)ModuleUIEnum.ELITE);
        if(costList == null)return;
        CommCostBaseVo costBaseVo = costList.get(id);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.ELITE_BUY_COUNT,null) == false)return;
        ssp.value--;
        AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
        user.setUserData(UserDataEnum.ELITE_BUY_COUNT,count-1,true);
        new EliteBuyCountRspd(client,mapID);
    }
}
