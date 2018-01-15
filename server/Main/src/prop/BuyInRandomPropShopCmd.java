package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.MissionConditionEnum;
import table.PropShopBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;


/**
 * Created by admin on 2016/8/3.
 */
public class BuyInRandomPropShopCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        BuyInRandomPropShopRqst rqst = (BuyInRandomPropShopRqst) baseRqst;
        buyInRandomPropShop(client,user,rqst.type,rqst.index);
    }

    private void buyInRandomPropShop(Client client, User user,byte type,byte index){
        PropShopBaseVo vo = Model.PropShopBaseMap.get((int)type).get(0);
        if(vo.needLevel < user.getUserData(UserDataEnum.LEVEL) && (vo.uiID != 0 && !user.isOpen((byte) vo.uiID)))return;
        PropShopPVo propShopPVo= null;
        if(vo.type != 0){
            propShopPVo= user.propModel.propShopMap.get(type);
            if(propShopPVo == null)return;
        }else{
            propShopPVo = user.propModel.trampShop;
        }
        if(propShopPVo == null)return;
        if(type != propShopPVo.type)return;
        ArrayList<ShopPropPVo> shopProplist = new ArrayList<>();
        shopProplist.addAll(propShopPVo.propList);
        ShopPropPVo shopPropPVo = shopProplist.get(index);
        if(shopPropPVo == null || shopPropPVo.isBuy == true)return;
        if(user.costUserDataAndProp(shopPropPVo.buyType,shopPropPVo.price,true, ReasonTypeEnum.BUY_IN_PROP_SHOP) == false)return;
        int[] arr = {shopPropPVo.propID,shopPropPVo.count};
        user.propModel.addListToBag(arr,ReasonTypeEnum.BUY_IN_PROP_SHOP);
        shopPropPVo.isBuy = true;
        propShopPVo.propList = shopProplist;
        user.activationModel.progressBuyAct(MissionConditionEnum.BUY_IN_RMBSHOP,0);
        new BuyInRandomPropShopRspd(client,type,index);
        user.propModel.saveSqlData();
    }
}
