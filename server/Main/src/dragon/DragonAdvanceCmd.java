package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AddDragonRspd;
import protocol.DragonAdvanceRqst;
import protocol.DragonPVo;
import protocol.DragonShowRspd;
import table.DragonAdvanceBaseVo;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/28.
 */
public class DragonAdvanceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DragonAdvanceRqst rqst = (DragonAdvanceRqst)baseRqst;
        DragonPVo targetVo=  user.cacheUserVo.dragonCacheModel.dragonsMap.get(rqst.baseID);
        if(targetVo == null)return;
        ArrayList<DragonAdvanceBaseVo> list = Model.DragonAdvanceBaseMap.get((int)rqst.baseID);
        if(list == null)return;
        DragonAdvanceBaseVo vo = null;
        if(targetVo.isActive){
            vo = list.get(targetVo.advance+1);
        }else{
            vo = list.get(targetVo.advance);
        }
        if(targetVo.count < vo.needCount)return;
        targetVo.count -= vo.needCount;
        if(targetVo.isActive){
            targetVo.advance++;
        }
        if(!targetVo.isActive){
            targetVo.isActive = true;
            targetVo.level = 1;
            user.addUserData(UserDataEnum.LJ_DRAGON_COUNT,1,true);
            new DragonShowRspd(user.client,targetVo.baseID);
        }
        new AddDragonRspd(client,targetVo);
        user.cacheUserVo.dragonCacheModel.saveSqlData();
        user.updateZDL();
    }
}
