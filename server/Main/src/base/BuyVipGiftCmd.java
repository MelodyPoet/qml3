package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.BuyVipGiftRqst;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;
import table.UserDataEnum;
import table.VipBaseVo;

/**
 * Created by admin on 2016/10/27.
 */
public class BuyVipGiftCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        BuyVipGiftRqst rqst = (BuyVipGiftRqst) baseRqst;
        buyVipGift(client,user,rqst.level);
    }

    private void buyVipGift(Client client,User user,byte level){
        if(user.buyVipGift != null && user.buyVipGift.indexOf(String.valueOf(level)) != -1)return;
        if(level > client.passportVo.vip)return;
        VipBaseVo vo = Model.VipBaseMap.get((int)level);
        if(vo == null)return;
        if(user.costUserDataAndProp(UserDataEnum.DIAMOND,vo.gift[2],true, ReasonTypeEnum.BUY_VIP_GIFT) == false)return;
        int[] arr = {vo.gift[0],1};
        user.propModel.addListToBag(arr,ReasonTypeEnum.BUY_VIP_GIFT);
        StringBuffer sb = new StringBuffer(user.buyVipGift);
        if(sb.length() == 0){
            sb.append(level);
        }else{
            sb.append("," + level);
        }
        user.buyVipGift = sb.toString();
        AllSql.userSql.update(user,AllSql.userSql.FIELD_BUY_VIP_GIFT,"'"+user.buyVipGift+"'");
    }
}
