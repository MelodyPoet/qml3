package skin;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.SkinPVo;
import protocol.SkinUpRqst;
import protocol.SkinUpRspd;
import table.ReasonTypeEnum;
import table.SkinAdvanceBaseVo;
import table.SkinUpBaseVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/4/12.
 */
public class SkinUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SkinUpRqst rqst = (SkinUpRqst)baseRqst;
        short skinID = rqst.skinID;
        HashMap<Short, SkinPVo> skinMap = user.cacheUserVo.skinModel.skinMap;
        if(!skinMap.containsKey(skinID))return;
        SkinPVo skinPVo = skinMap.get(skinID);
        ArrayList<SkinUpBaseVo> skinUpList = Model.SkinUpBaseMap.get((int)skinID);
        if(skinPVo.level >= skinUpList.size())return;
        SkinUpBaseVo skinUpBaseVo = skinUpList.get(skinPVo.level);
        ArrayList<SkinAdvanceBaseVo> advaceList = Model.SkinAdvanceBaseMap.get((int)skinID);
        if(skinPVo.advance >= advaceList.size()-1){
            if(skinPVo.level >= skinUpList.size())return;
        }else{
            SkinAdvanceBaseVo skinAdvanceBaseVo = advaceList.get(skinPVo.advance+1);
            if(skinPVo.level >= skinAdvanceBaseVo.needlevel)return;
        }
        if(user.costUserDataAndPropList(skinUpBaseVo.costItems,true, ReasonTypeEnum.SKIN_UP,null)==false)return;
        skinPVo.level++;
        new SkinUpRspd(client,skinID,skinPVo.level);
        user.cacheUserVo.skinModel.saveSqlData();
    }
}
