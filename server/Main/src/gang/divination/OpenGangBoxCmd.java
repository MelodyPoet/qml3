package gang.divination;

import base.BaseModel;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.GangBoxBaseVo;
import table.ReasonTypeEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/14.
 */
public class OpenGangBoxCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
        if(vo == null)return;
        OpenGangBoxRqst rqst = (OpenGangBoxRqst)baseRqst;
        GangBoxBaseVo gangBoxBaseVo = Model.GangBoxBaseMap.get((int)rqst.boxID);
        if(gangBoxBaseVo == null)return;
        if(GameSetModel.checkTime(1))return;
        if(vo.luckValue < gangBoxBaseVo.luckValue)return;
        for(int id : vo.getBoxID){
            if(id == rqst.boxID)return;
        }
        ArrayList<PropPVo> drops = BaseModel.getDropProps(gangBoxBaseVo.dropID,0);
        ArrayList<IntIntPVo> list = new ArrayList<>();
        for(PropPVo propPVo : drops){
            GangGiftLogPVo giftLog = new GangGiftLogPVo();
            giftLog.name = user.cacheUserVo.name;
            giftLog.propId = propPVo.baseID;
            giftLog.time = JkTools.getGameServerTime(client);
            gangVo.giftLogList.add(0,giftLog);
            IntIntPVo intIntPVo = new IntIntPVo();
            intIntPVo.key = propPVo.baseID;
            intIntPVo.value = propPVo.count;
            list.add(intIntPVo);
            if(gangVo.giftMap.containsKey(propPVo.baseID)){
                IntIntPVo pVo = gangVo.giftMap.get(propPVo.baseID);
                pVo.value += propPVo.count;
            }else{
                gangVo.giftMap.put(intIntPVo.key,intIntPVo);
            }
        }
        if(drops.size() > 0){
            gangVo.saveGangGiftList();
            gangVo.saveGangGiftLog();
        }
        user.propModel.addListToBag(gangBoxBaseVo.prop, ReasonTypeEnum.OPEN_GANG_BOX);
        vo.getBoxID.add((int)rqst.boxID);
        vo.saveGetBoxID();
        new AddGiftRspd(client,list);
        new OpenGangBoxRspd(client,rqst.boxID);
    }
}
