package skin;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SkinAdvanceRqst;
import protocol.SkinAdvanceRspd;
import protocol.SkinPVo;
import table.ReasonTypeEnum;
import table.SkinAdvanceBaseVo;
import table.SkinBaseVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/4/12.
 */
public class SkinAdvanceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SkinAdvanceRqst rqst = (SkinAdvanceRqst)baseRqst;
        SkinBaseVo skinBaseVo = Model.SkinBaseMap.get((int)rqst.skinID);
        if(skinBaseVo == null)return;
        HashMap<Short, SkinPVo> skinMap = user.cacheUserVo.skinModel.skinMap;
        SkinPVo skinPVo = new SkinPVo();
        short skinID = rqst.skinID;
        if(skinMap.containsKey(skinID)){
            skinPVo = skinMap.get(skinID);
        }else{
            skinPVo.advance = -1;
        }
        ArrayList<SkinAdvanceBaseVo> skinAdvanceList = Model.SkinAdvanceBaseMap.get((int)skinID);
        if(skinPVo.advance >= skinAdvanceList.size()-1)return;
        SkinAdvanceBaseVo nextAdvanceVo = skinAdvanceList.get(skinPVo.advance+1);
        if(nextAdvanceVo == null)return;
        if(skinPVo.level != nextAdvanceVo.needlevel)return;
        if(user.costUserDataAndPropList(nextAdvanceVo.costItems,true, ReasonTypeEnum.SKIN_ADVANCE,null) == false)return;
        if(skinPVo.advance == -1){
            skinPVo.level = 1;
            skinPVo.skinID = skinID;
            skinMap.put(skinID,skinPVo);
        }
        skinPVo.advance++;
        new SkinAdvanceRspd(client,skinID,skinPVo.advance);
        user.cacheUserVo.skinModel.saveSqlData();
    }
}
