package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.*;

import java.util.ArrayList;


public class BuyInShopCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        BuyInShopRqst rqst= (BuyInShopRqst) baseRqst;
        ShopBaseVo shopVo= Model.ShopBaseMap.get(rqst.shopID);
        if (shopVo==null||shopVo.shopID<0)return;
            if(shopVo.countInUserdata>0){
                if(user.enoughForcostUserDataAndProp(shopVo.countInUserdata, 1)==false)return;

            }
                if (!user.costUserDataAndPropList(shopVo.costUserdata, true, ReasonTypeEnum.BUY_IN_SHOP,null)) {
                    return;
                }

        user.costUserDataAndProp( shopVo.countInUserdata, 1, true,ReasonTypeEnum.BUY_IN_SHOP);
     //   DCGameLog.getInstance().AddLog(GameLogType.COINLOST, new String[]{user.guid + "",(byte)shopVo.moneyType + "", (totalPrice)  + "", user.getUserData((byte)shopVo.moneyType) + "", "buyInShop"});
user.activationModel.progressBuyAct(MissionConditionEnum.BUY_IN_RMBSHOP,shopVo.ID);
        user.activationModel.progressBuyAct(MissionConditionEnum.BUY_IN_RMBSHOP,0);
        if(shopVo.shopID == 6){
          //  user.worldBossModel.buffSet.add(shopVo.count);
        //    new WB_BuffRspd(client,user.worldBossModel.buffSet);
            return;
        }
        PropBaseVo propBaseVo=Model.PropBaseMap.get(shopVo.propID);
        if(propBaseVo.type==PropTypeEnum.USER_DATA) {
          int rstVal=  user.addUserData((byte) propBaseVo.ID, shopVo.count, true);
         //       DCGameLog.getInstance().AddLog(GameLogType.COINGAIN, new String[]{user.guid + "", propBaseVo.effect[0] + "", (shopVo.count*buyCount)  + "", rstVal + "", "buyInShop"});

            }else{
            ArrayList<PropPVo> list = new ArrayList<>();
            PropPVo pvo=new PropPVo();
            pvo.baseID=propBaseVo.ID;
            pvo.count=shopVo.count;
            list.add(pvo);
            user.propModel.addListToBag(list,ReasonTypeEnum.BUY_IN_SHOP);
//           new AddPropRspd(client, PropCellEnum.BAG,user.propModel.addInBag(pvo,true));
              //  DCGameLog.getInstance().AddLog(GameLogType.PROPBUY, new String[]{user.guid + "",pvo.baseID+"","shopProp",shopVo.count*buyCount+"",totalPrice+"",shopVo.moneyType+""});


            }

    }

	 

}
