package base;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.MapTypeEnum;
import table.PropTypeEnum;
import table.ReasonTypeEnum;

import java.util.ArrayList;


public class MapAttackCmd extends BaseRqstCmd {
    public static byte isCheck;

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        MapAttackRqst rqst= (MapAttackRqst) baseRqst;
        isCheck = rqst.isCheckBag;
        MapQualityPVo mapQualityPVo = user.mapQualityMap.get(rqst.mapID);
        if (mapQualityPVo == null)return;
        System.out.println("======mapQualityPVo");
PropPVo stone= user.propModel.bagItems.get(rqst.stoneID);
        if(stone==null||Model.PropBaseMap.get(stone.baseID).type!= PropTypeEnum.MOPPING_UP)return;
        if(Model.MapBaseMap.get(rqst.mapID).type!= MapTypeEnum.NORMAL)return;
      //  if(user.mapQualityMap.get(rqst.mapID).stars<3)return;
        if(new GoMapCmd(true).execute(client,user,rqst.mapID,false)==false)return;
        System.out.println("======GoMapCmd");
       int exp= user.fightingLoot.totalExp;
        user.costUserDataAndProp(stone.baseID,1,true, ReasonTypeEnum.MAP_ATTACK);
        GetLootRqst getLootRqst=new GetLootRqst();
        user.actState = UserActState.FIGHT;
        getLootRqst.stars = new ArrayList<>();
        getLootRqst.stars.addAll(mapQualityPVo.stars);
        getLootRqst.dynamicProps = new ArrayList<>();
        new GetLootCmd(true).execute(client, user, getLootRqst, false);
new MapAttackRspd(client,exp);
        System.out.println("======MapAttackRspd");

    }


}
