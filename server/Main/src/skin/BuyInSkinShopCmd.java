package skin;

import airing.AiringModel;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.AiringMessagePVo;
import protocol.BuyInSkinShopRqst;
import protocol.SkinPVo;
import table.HonorAiringBaseVo;
import table.ReasonTypeEnum;
import table.RoleBaseVo;
import table.SkinBaseVo;

import java.util.ArrayList;

/**
 * Created by admin on 2016/8/26.
 */
public class BuyInSkinShopCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        BuyInSkinShopRqst rqst = (BuyInSkinShopRqst)baseRqst;
        buyInSkinShop(client,user,rqst.skinID);
    }

    private void buyInSkinShop(Client client, User user,short skinID){
        SkinBaseVo vo = Model.SkinBaseMap.get((int)skinID);
        if(vo == null)return;
        if(vo.heroID != user.baseID)return;
        SkinModel skinModel = user.cacheUserVo.skinModel;
        if(skinModel.skinMap.containsKey(skinID))return;
        if(vo.costUserdata != null){
            if(user.costUserDataAndProp(vo.costUserdata[0],vo.costUserdata[1],true, ReasonTypeEnum.BUY_IN_SKIN_SHOP) == false)return;
        }
        SkinPVo skinPVo = new SkinPVo();
        skinPVo.skinID = skinID;
        skinPVo.level = 1;
        skinPVo.advance = 0;
        skinModel.skinMap.put(skinID,skinPVo);
        skinModel.saveSqlData();

        airing(user,skinID);
    }

    private static void airing(User user,short skinID){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(3);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int level = ((int)skinID-1)%20+1;
        if(level%airingVo.divisor != 0)return;
        if(JkTools.compare(level,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&& JkTools.compare(level,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        RoleBaseVo vo = Model.RoleBaseMap.get(user.baseID);
        if(vo == null)return;
        SkinBaseVo skinBaseVo = Model.SkinBaseMap.get(skinID);
        if(skinBaseVo == null)return;
//        pVo.msg = "恭喜 "+user.cacheUserVo.passportVo.name+" 获得 "+vo.name+" 皮肤 "+ skinBaseVo.name +" 。";
        pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",vo.name).replace("{3}",skinBaseVo.name);
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
