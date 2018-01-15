package gang.divination;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.GetGangGiftBoxRqst;
import protocol.IntIntPVo;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/14.
 */
public class GetGangGiftBoxCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
        if(vo == null)return;
        if(vo.isGetGift == 1)return;
        if(!GameSetModel.checkTime(1))return;
        ArrayList<PropPVo> list = new ArrayList<>();
        for(IntIntPVo intIntPVo : gangVo.giftMap.values()){
            PropPVo propPVo = new PropPVo();
            propPVo.baseID = intIntPVo.key;
            propPVo.count = intIntPVo.value;
            list.add(propPVo);
        }
        user.propModel.addListToBag(list, ReasonTypeEnum.GANG_GIFT_BOX);
        vo.isGetGift = 1;
        AllSql.gangMemberSql.update(vo,AllSql.gangMemberSql.FIELD_IS_GET_GIFT,vo.isGetGift);
        new GeneralSuccessRspd(client, GetGangGiftBoxRqst.PRO_ID);
    }
}
