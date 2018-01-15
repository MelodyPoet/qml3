package dragon;

import airing.AiringModel;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;


public class DragonStoneUpCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        DragonStoneUpRqst rqst = (DragonStoneUpRqst) baseRqst;
       PropPVo pvo= user.propModel.getPropInBag(rqst.tempID);

        if(pvo==null|| Model.DragonStoneUpBaseMap.containsKey(pvo.baseID)==false){
            return;
        }

        DragonStoneUpBaseVo upBaseVo= Model.DragonStoneUpBaseMap.get(pvo.baseID);
        if(upBaseVo == null)return;
        if(upBaseVo.itemCost == null)return;
if(pvo.count<3){
    return;

}
        int count = 1;
        if(rqst.type == 1){
            count = pvo.count/upBaseVo.needCount;
            if(upBaseVo.itemCost.length>=4){
                for (PropPVo propPVo :user.propModel.bagItems.values()){
                    if(pvo.baseID!=upBaseVo.itemCost[2])continue;
                    count = Math.min(count,propPVo.count/upBaseVo.itemCost[3]);
                }
            }
            if(upBaseVo.itemCost.length>=2) {
                count = Math.min(count, user.getUserData((byte) upBaseVo.itemCost[0]) / upBaseVo.itemCost[1]);
            }
            if(count<=0)return;
            if(upBaseVo.itemCost.length>=2) {
                upBaseVo.itemCost[1] *= count;
            }
            if(upBaseVo.itemCost.length>=4) {
                upBaseVo.itemCost[3] *= count;
            }
        }
        if(user.costUserDataAndPropList(upBaseVo.itemCost,true,ReasonTypeEnum.DRAGON_STONE_UP, null)==false)return;
        user.costUserDataAndProp(pvo.baseID,upBaseVo.needCount*count,true,ReasonTypeEnum.DRAGON_STONE_UP);
        PropPVo newVo=new PropPVo();

        newVo.baseID=Model.PropBaseMap.get(pvo.baseID).effect[0];
        newVo.count=count;
        newVo=user.propModel.addInBag(newVo,true,true,ReasonTypeEnum.DRAGON_STONE_UP);
        new AddPropRspd(client, PropCellEnum.BAG,newVo);
        new GeneralSuccessRspd(client, rqst.protocolID);

        user.activationModel.progressBuyAct(MissionConditionEnum.DragonStoneCompose,0);
        airing(user.cacheUserVo.name,newVo.baseID);
        user.addUserData(UserDataEnum.LJ_UP_DRAGON_STONE_COUNT,1,true);

        int level = newVo.baseID%100+1;
        if(user.dragonStoneOwnLevel.indexOf(String.valueOf(level)) == -1){
            StringBuffer sb = new StringBuffer(user.dragonStoneOwnLevel);
            if(sb.length() == 0){
                sb.append(level);
            }else{
                sb.append("," + level);
            }
            user.dragonStoneOwnLevel = sb.toString();
            AllSql.userSql.update(user,AllSql.userSql.FIELD_OWN_DRAGON_STONE_LEVEL,"'"+user.dragonStoneOwnLevel+"'");
            new DragonStoneOwnLevelRspd(client,sb.toString());
        }
    }

    private static void airing(String name,int baseID){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(10);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int level = baseID%100+1;
        if(level%airingVo.divisor != 0)return;
        if(JkTools.compare(level,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&& JkTools.compare(level,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        PropBaseVo vo = Model.PropBaseMap.get(baseID);
        if(vo == null)return;
//        pVo.msg = "恭喜 "+name+" 合成了 "+vo.name+" 。";
        pVo.msg = airingVo.msg.replace("{1}",name).replace("{2}",vo.name);
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
