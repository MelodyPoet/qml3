package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import mission.MissionModel;
import prop.PropModel;
import protocol.*;
import table.MissionBaseVo;
import table.MissionTypeEnum;
import table.ReasonTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Created by admin on 2016/10/9.
 */
public class GetAllMissionCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetAllMissionRqst rqst = (GetAllMissionRqst) baseRqst;
        getAllMission(client,user,rqst.type,(List<Integer>) rqst.canGetList);
    }
    private void getAllMission(Client client, User user,byte type, List<Integer> canGetList){
        HashSet<Integer> set = new HashSet<>();
        ArrayList<PropPVo> list = new ArrayList<>();
        MissionModel msModel= null;
        if(type == MissionTypeEnum.MAIN){
            msModel = user.missionModel;
        }
        if(type == MissionTypeEnum.DAILY){
            msModel = user.activationModel;
        }
        if(type == MissionTypeEnum.ACHIEVE){
            msModel = user.achieveModel;
        }
        if(msModel == null){
            return;
        }

        for(Integer id : canGetList){
            MissionBaseVo missionBaseVo= Model.MissionBaseMap.get(id);
            if(missionBaseVo==null)return;

            if(msModel.canComplete(missionBaseVo)!=-1)continue;
            if(  msModel!=user.achieveModel) {
                msModel.acceptedList.remove(missionBaseVo);
            }

            msModel.completedList.add(missionBaseVo.ID);

            for(int i=0;i<missionBaseVo.prop.length;i+=2){
                int whiteID = missionBaseVo.prop[i];
                if(PropModel.isEquip(whiteID)){
                    whiteID=whiteID%1000+user.cacheUserVo.baseID*1000;
                    missionBaseVo.prop[i] = whiteID;
                }
                PropPVo propPVo = new PropPVo();
                propPVo.baseID = missionBaseVo.prop[i];
                propPVo.count = missionBaseVo.prop[i+1];
                list.add(propPVo);
            }
            set.add(missionBaseVo.ID);
        }
        user.propModel.addListToBag(list, ReasonTypeEnum.COMPLETE_MISSION);
        new GetAllMissionRspd(client,type,set);
        if(type==MissionTypeEnum.MAIN) {
            user.missionModel.calculateNewMission();
        }
        msModel.saveSqlData();
    }
}
