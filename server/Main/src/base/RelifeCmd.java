package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.RelifeRqst;
import protocol.RelifeRspd;
import table.ReasonTypeEnum;
import table.ShopBaseVo;

/**
 * Created by admin on 2016/10/24.
 */
public class RelifeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RelifeRqst rqst = (RelifeRqst) baseRqst;
        relife(client,user);
    }

    private void relife(Client client,User user){
        if(user.fightingLoot == null || user.fightingLoot.relifeCount <= 0){
            new RelifeRspd(client,(byte) 2);
            return;
        }
        if(user.fightingLoot.freeRelifeCount > 0){
            user.fightingLoot.freeRelifeCount--;
        }else{
            boolean flag = false;
            if(user.costUserDataAndProp(12024,1,true,ReasonTypeEnum.RELIFE) == true){
                flag = true;
            }
            if(!flag){
                ShopBaseVo shopBaseVo = Model.ShopBaseMap.get(73);
                if(user.costUserDataAndProp(shopBaseVo.costUserdata[0],shopBaseVo.costUserdata[1],true,ReasonTypeEnum.RELIFE)==false){
                    new RelifeRspd(client,(byte) 1);
                    return;
                }
            }
        }
        user.fightingLoot.relifeCount--;
        new GeneralSuccessRspd(client,RelifeRqst.PRO_ID);
    }
}
