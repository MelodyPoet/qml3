package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.TalentPVo;
import protocol.TalentUpRqst;
import protocol.TalentUpRspd;
import table.*;

import java.util.ArrayList;


public class TalentUpCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        TalentUpRqst rqst = (TalentUpRqst) baseRqst;
        ArrayList<TalentBaseVo> baseVos = Model.TalentBaseMap.get((int) rqst.ID);
        if (baseVos == null) return;

        TalentPVo talentPVo = user.cacheUserVo.skillModel.talents.get(rqst.ID);
        if (talentPVo == null) {
            talentPVo = new TalentPVo();
            talentPVo.baseID = rqst.ID;
            talentPVo.level = 0;
        }
        //取消CD
//        int time = JkTools.getGameServerTime(client) - user.getUserData(UserDataEnum.Talent_CD);
//        int limit = ((user.getUserData(UserDataEnum.LEVEL)-1)/10+1)*60*60;
//        if(-time > limit){
//            return;
//        }
        if (talentPVo.level >= baseVos.size() - 1) return;
        TalentBaseVo baseVo = baseVos.get(talentPVo.level);
        if (baseVo.openLevel > 0 && user.getUserData(UserDataEnum.LEVEL) < baseVo.openLevel) return;
        int cost = baseVo.costUserdata[1];
        int value = user.getUserData(UserDataEnum.TALENT_EXP);
        int count = 0;
        for (int i = 0, len = rqst.type == 1 ? 1000000 : 1; i < len; i++) {
            if(value < cost)break;
            count++;
            value -= cost;
            talentPVo.exp += cost;
            if (talentPVo.exp >= baseVo.costPower) break;
        }
        user.setUserData(UserDataEnum.TALENT_EXP,value,true);
//        if(talentPVo.level == 1){
//            if(time > 0){
//                user.setUserData(UserDataEnum.Talent_CD, JkTools.getGameServerTime(client)+baseVo.cd,true);
//            }else{
//                user.addUserData(UserDataEnum.Talent_CD,baseVo.cd,true);
//            }
//
//        }else{
//            if(time > 0){
//                user.setUserData(UserDataEnum.Talent_CD, JkTools.getGameServerTime(client)+baseVo.cd,true);
//            }else{
//                user.addUserData(UserDataEnum.Talent_CD,baseVo.cd,true);
//            }
//        }

        user.cacheUserVo.skillModel.talents.put(talentPVo.baseID, talentPVo);
        user.cacheUserVo.skillModel.saveSqlData();
        // user.missionModel.progressBuyAct(MissionActEnum.LEVELUP_TALENT);
//        new GeneralSuccessRspd(client,baseRqst.protocolID);
        if(cost > 0 && count > 0){
            user.activationModel.progressBuyAct(MissionConditionEnum.Talent, 0);
        }
        if (talentPVo.exp >= baseVo.costPower) {
            talentPVo.level++;
            talentPVo.exp -= baseVo.costPower;
            user.updateZDL();
            user.addUserData(UserDataEnum.LJ_TALENT_UP_COUNT, 1, true);
            if (talentPVo.level > 1) {
                if (talentPVo.level > user.getUserData(UserDataEnum.MAX_TALENT_LEVEL)) {
                    user.setUserData(UserDataEnum.MAX_TALENT_LEVEL, talentPVo.level, true);
                }
            }
        }
        new TalentUpRspd(client, talentPVo);
    }
}
