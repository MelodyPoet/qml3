package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import table.CommCostBaseVo;
import table.ModuleUIEnum;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/6.
 */
public class OpenEquipBagCellCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        int hadUsedCount = user.getUserData(UserDataEnum.OPEN_EQUIP_BAG_COUNT);
        ArrayList<CommCostBaseVo> list = Model.CommCostBaseMap.get((int)ModuleUIEnum.HERO_INFO);
        if(list == null)return;
        if(hadUsedCount >= list.size())return;
        CommCostBaseVo costBaseVo = list.get(hadUsedCount);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true, ReasonTypeEnum.OPEN_QUIP_BAG_CELL,null) == false)return;
        user.propModel.addListToBag(costBaseVo.extra,ReasonTypeEnum.OPEN_QUIP_BAG_CELL);
        user.addUserData(UserDataEnum.OPEN_EQUIP_BAG_COUNT,1,true);
    }
}
