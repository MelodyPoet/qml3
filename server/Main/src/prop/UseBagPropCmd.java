package prop;
import base.BaseModel;
import gluffy.utils.JkTools;
import talk.TalkCmd;
import gluffy.comm.BaseRqst;
import protocol.*;
import sqlCmd.AllSql;
import table.*;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;

import java.util.ArrayList;
import java.util.List;


public class UseBagPropCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
		UseBagPropRqst rqst=(UseBagPropRqst) baseRqst;
        if(rqst.count<=0)rqst.count=1;

        PropPVo propPVo=user.propModel.getPropInBag(rqst.tempID);
		if(propPVo==null){
			return;
		}
        if(rqst.count>propPVo.count)rqst.count=(short)propPVo.count;

	PropBaseVo baseVo = Model.PropBaseMap.get(propPVo.baseID);

        if(PropModel.isEquip(propPVo.baseID)) {
            if (baseVo.level > user.getUserData(UserDataEnum.LEVEL)) return;

            ArrayList<CommUnlockBaseVo> commUnlockList = Model.CommUnlockBaseMap.get((int)ModuleUIEnum.HERO_INFO);
            CommUnlockBaseVo commUnlockBaseVo = commUnlockList.get(baseVo.type-1);
            if (commUnlockBaseVo.needUserLv > user.getUserData(UserDataEnum.LEVEL)) return;



            PropPVo oldEquipVo = user.propModel.getEquipByIndex((byte) baseVo.type);

            AllSql.propSql.update(AllSql.propSql.FIELD_TABLEID, PropCellEnum.EQUIP + "", propPVo.tempID);
            user.propModel.deleteBag(propPVo.tempID,true,ReasonTypeEnum.USE_BAG_PROP);
            user.propModel.setEquipByIndex((byte) baseVo.type, propPVo);
            if (oldEquipVo != null) {
                AllSql.propSql.update(AllSql.propSql.FIELD_TABLEID, PropCellEnum.BAG + "", oldEquipVo.tempID);
                user.propModel.addInBag(oldEquipVo, false,true,ReasonTypeEnum.USE_BAG_PROP);
            }
            user.updateZDL();
            new EquipOnRspd(client, rqst.tempID);
            new GeneralSuccessRspd(client, UseBagPropRqst.PRO_ID);
        }else {
            if (baseVo.level > user.getUserData(UserDataEnum.LEVEL)) {
                new ServerTipRspd(client,(short)319,String.valueOf(baseVo.level));
                return;
            }
            execute(client,user,baseVo,rqst.count);
            user.costUserDataAndProp(propPVo.baseID, rqst.count,true,ReasonTypeEnum.USE_BAG_PROP);
        }

		
	}

	 public void execute(Client client,User user,PropBaseVo baseVo,int count){



             //if(baseVo.type== PropTypeEnum.MAIN_WEAPON){
             //  user.missionModel.progressBuyAct(MissionActEnum.EQUIP_WEAPON);
             //}


             switch (baseVo.type) {
                 case PropTypeEnum.GM_CMD:

                     TalkRqst talkRqst=    new TalkRqst();
                     talkRqst.msg=baseVo.gmcmd;
                    String[] params= baseVo.gmcmd.split(" ");
                     if("/addudata".equals(params[0])){
                         params[2]=(Integer.parseInt(params[2])*count)+"";
                         talkRqst.msg=params[0]+" "+params[1] +" "+params[2];

                     }

                         new TalkCmd().execute(client,user,talkRqst);


                     break;
                 case PropTypeEnum.GROUP:
                 {
                     List<PropPVo> props=new ArrayList<>();
                     for (int i = 0; i < baseVo.effect.length; i += 2) {
                         PropPVo pvo = new PropPVo();
                         pvo.baseID =  baseVo.effect[i];
                         pvo.count =  baseVo.effect[i+1]*count;
                         props.add(pvo);

                     }
                     user.propModel.addListToBag(props,ReasonTypeEnum.USE_BAG_PROP);
                     new AwardShowRspd(client,props);

                 }
                 break;
                 case PropTypeEnum.RANDOM_GIFT:
                 {
                     List<PropPVo> props = new ArrayList<>();
                     for(int i=0;i<count;i++){
                         props.addAll(BaseModel.getDropProps(baseVo.effect,user.baseID));
                     }
                     user.propModel.addListToBag(props,ReasonTypeEnum.USE_BAG_PROP);
                     new AwardShowRspd(client,props);
                 }
                 break;
                 case PropTypeEnum.MIANZHAN:
                 {

                     int lastTime=user.getUserData(UserDataEnum.NEXTTIME_SNATCH_SAFE);
                     lastTime=Math.max(lastTime, JkTools.getGameServerTime(null));
                     user.setUserData(UserDataEnum.NEXTTIME_SNATCH_SAFE,lastTime+baseVo.effect[0],true);

                 }
                 break;
                 default:
                     return;
             }




     }

}
