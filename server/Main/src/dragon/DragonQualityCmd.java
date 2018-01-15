package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AddDragonRspd;
import protocol.DragonPVo;
import protocol.DragonQualityRqst;
import table.DragonQualityBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/8/4.
 */
public class DragonQualityCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DragonQualityRqst rqst = (DragonQualityRqst)baseRqst;
        DragonPVo targetVo=  user.cacheUserVo.dragonCacheModel.dragonsMap.get(rqst.baseID);
        if(targetVo == null || !targetVo.isActive)return;
        ArrayList<DragonQualityBaseVo> list = Model.DragonQualityBaseMap.get((int)rqst.baseID);
        if(list == null)return;
        if(targetVo.quality >= list.size())return;
        DragonQualityBaseVo vo = list.get(targetVo.quality);
        if(user.costUserDataAndPropList(vo.costUserdata,true, ReasonTypeEnum.DRAGON_QUALITY,null) == false)return;
        targetVo.quality++;
        new AddDragonRspd(client,targetVo);
        user.cacheUserVo.dragonCacheModel.saveSqlData();
    }
}
