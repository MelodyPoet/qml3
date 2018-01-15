package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GetVipGiftRqst;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;
import table.VipBaseVo;

/**
 * Created by admin on 2016/10/25.
 */
public class GetVipGiftCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetVipGiftRqst rqst = (GetVipGiftRqst)baseRqst;
        getVipGift(client,user,rqst.level);
    }

    private void getVipGift(Client client,User user,byte level){
        if(client.passportVo.getVipAward != null && client.passportVo.getVipAward.indexOf(String.valueOf(level)) != -1)return;
        if(level > client.passportVo.vip)return;
        VipBaseVo vo = Model.VipBaseMap.get((int)level);
        if(vo == null)return;
        user.propModel.addListToBag(vo.reward, ReasonTypeEnum.GET_VIP_GIFT);
        StringBuffer sb = new StringBuffer(client.passportVo.getVipAward);
        if(sb.length() == 0){
            sb.append(level);
        }else{
            sb.append("," + level);
        }
        client.passportVo.getVipAward = sb.toString();
        AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_GET_VIP_AWARD,"'"+client.passportVo.getVipAward+"'");
    }
}
