package mission;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import prop.PropModel;
import protocol.MissionCompleteRqst;
import protocol.MissionCompleteRspd;
import table.MissionBaseVo;
import table.MissionTypeEnum;
import table.ReasonTypeEnum;


public class MissionCompleteCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        MissionCompleteRqst rqst = (MissionCompleteRqst) baseRqst;
        MissionBaseVo missionBaseVo=Model.MissionBaseMap.get(rqst.msID);
        if(missionBaseVo==null)return;
        MissionModel msModel=user.missionModel;
        byte type = (byte)missionBaseVo.type;
        switch (type){

            case MissionTypeEnum.DAILY:
                msModel=user.activationModel;
                break;
            case MissionTypeEnum.ACHIEVE:
                msModel=user.achieveModel;
                break;
            default:
                if(user.limitTimeMap.containsKey(type)){
                   msModel = user.limitTimeMap.get(type);
                }else {
                    if(missionBaseVo.type > MissionTypeEnum.ACHIEVE){//限时活动未启用不能完成
                        if(!MissionModel.isActive(user,missionBaseVo.type))return;
                    }
                    msModel = new MissionModel(user,type);
                    user.limitTimeMap.put(type,msModel);
                }
        }


        if(rqst.isCost){
            if(user.costUserDataAndPropList(missionBaseVo.cost,true, ReasonTypeEnum.COMPLETE_MISSION,null)== false)return;
        }else{
            if(msModel.canComplete(missionBaseVo)!=-1)return;
        }
        if(  msModel!=user.achieveModel) {
            msModel.acceptedList.remove(missionBaseVo);
        }

            msModel.completedList.add(missionBaseVo.ID);

//        for(int i=0;i<missionBaseVo.prop.length;i+=2){
//            int whiteID = missionBaseVo.prop[i];
//            if(PropModel.isEquip(whiteID)){
//                whiteID=whiteID%1000+user.cacheUserVo.baseID*1000;
//                missionBaseVo.prop[i] = whiteID;
//            }
//        }
        int[] props = missionBaseVo.prop;
        if(props.length == 1){
            props = Model.CommRewardPropBaseMap.get(props[0]).get((user.cacheUserVo.level-1)/10).prop;
        }
        if(missionBaseVo.type > MissionTypeEnum.ACHIEVE){
            for(int i=0;i<props.length;i+=2){
                if(PropModel.isIntegral(props[i])){
                    if(msModel.propMap.containsKey(props[i])){
                        msModel.propMap.put(props[i],props[i+1]+msModel.propMap.get(props[i]));
                    }else{
                        msModel.propMap.put(props[i],props[i+1]);
                    }
                }
            }
        }
        user.propModel.addListToBag(props,ReasonTypeEnum.COMPLETE_MISSION);




        new MissionCompleteRspd(client,missionBaseVo.ID);
        if(missionBaseVo.type==MissionTypeEnum.MAIN) {
            user.missionModel.calculateNewMission();
        }

        msModel.saveSqlData();

    }

	 

}
