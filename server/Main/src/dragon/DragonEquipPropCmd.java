package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.*;

import java.util.HashMap;


public class DragonEquipPropCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        DragonEquipPropRqst rqst = (DragonEquipPropRqst) baseRqst;
        if (rqst.index < 0 || rqst.index > 39) return;

        HashMap<Byte, StonePVo> equipDragonStone = user.cacheUserVo.equipDragonStone;
        StonePVo stonePVo = new StonePVo();
        stonePVo.index = rqst.index;
        if (rqst.isModeOn) {
            PropPVo pvo = user.propModel.getPropInBag(rqst.tempID);
            if (pvo == null) return;
            if (pvo.count < 1) return;
            PropBaseVo baseVo = Model.PropBaseMap.get(pvo.baseID);
            if ((rqst.index % 5 + 1) != baseVo.level) return;
            if (equipDragonStone.containsKey(rqst.index)) {
                StonePVo lastPVo = user.cacheUserVo.equipDragonStone.get(rqst.index);
                if (lastPVo == null) return;
                user.propModel.addListToBag(new int[]{lastPVo.baseID,1},ReasonTypeEnum.DRAGON_EQUIP_PROP);
                equipDragonStone.remove(rqst.index);
            }
            user.costUserDataAndProp(pvo.baseID, 1, true,ReasonTypeEnum.DRAGON_EQUIP_PROP);
            stonePVo.baseID = pvo.baseID;
            user.cacheUserVo.equipDragonStone.put(rqst.index, stonePVo);
            new DragonStoneEquipOnRspd(client, stonePVo);
        } else {
            if (!equipDragonStone.containsKey(rqst.index)) return;
            StonePVo lastPVo = user.cacheUserVo.equipDragonStone.get(rqst.index);
            if (lastPVo == null) return;
            user.propModel.addListToBag(new int[]{lastPVo.baseID,1},ReasonTypeEnum.DRAGON_EQUIP_PROP);
            equipDragonStone.remove(rqst.index);
            new EquipOffRspd(client, stonePVo);
        }
        user.dragonModel.saveDragonStoneEquip();
        user.updateZDL();

    }
//        ||user.dragonModel.dragonsMap.containsKey(rqst.mainTempID)==false){
//        user.dragonModel.mainDragon=user.dragonModel.dragonsMap.get(rqst.mainTempID);
//        ArrayList<Long> moveToBag=new ArrayList<>();
//        ArrayList<Long> moveToEquip=new ArrayList<>();
//
//
//        for (long tempID : user.dragonModel.equipItems.keySet()){
//            if(rqst.propTempIDs.indexOf(tempID)<0)moveToBag.add(tempID);
//        }
//        for (long tempID :rqst.propTempIDs){
//            if(user.dragonModel.equipItems.containsKey(tempID)==false)moveToEquip.add(tempID);
//        }
//
//          for (long tempID : moveToBag ){
//          PropPVo pvo= user.dragonModel.equipItems.remove(tempID);
//             if(pvo!=null) {
//                 user.propModel.addInBag(pvo, true);
//             }
//
//        }
//        for (long tempID : moveToEquip ){
//            PropPVo pvo= user.propModel.getPropInBag(tempID);
//            if(pvo==null) {continue;}
//
//            if(pvo.count==1){
//                user.propModel.bagItems.remove(tempID);
//                user.dragonModel.equipItems.put(pvo.tempID, pvo);
//                AllSql.propSql.update(AllSql.propSql.FIELD_TABLEID, PropCellEnum.DRAGON_STONE+"",pvo.tempID);
//            }else {
//
//                user.propModel.deleteBag(pvo,1,false);
//                user.dragonModel.equipItems.put(pvo.tempID, pvo);
//            }
//
//
//
//        }
//        new GeneralResultRspd(client,true);

   // }
	 

}
