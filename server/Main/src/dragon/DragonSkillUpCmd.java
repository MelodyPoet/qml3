package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.ReasonTypeEnum;
import table.SkillBaseVo;
import table.UserDataEnum;

import java.util.ArrayList;

/*
 * Created by jackie on 14-5-6.
 */
public class DragonSkillUpCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        DragonSkillUpRqst rqst=(DragonSkillUpRqst) baseRqst;

        DragonPVo dragonPVo= user.cacheUserVo.dragonCacheModel.dragonsMap.get(rqst.baseID);
        ArrayList<DragonTalentPVo> talent  = user.dragonModel.dragonTalentMap.get(rqst.baseID);
        if(dragonPVo==null)return;
        if(!dragonPVo.isActive)return;
        int skillID = rqst.skillID;
        byte level = 0;
        boolean isGameBreaker = false;
        DragonTalentPVo dragonTalentPVo = null;
        if(skillID == Model.DragonBaseMap.get((int) dragonPVo.baseID).gameBreaker){//升级必杀技
            if(level >= user.getUserData(UserDataEnum.LEVEL))return;
            level = dragonPVo.skillLevel;
            isGameBreaker = true;
        }else{//升级天赋
            if(talent != null){
                for(DragonTalentPVo talentPVo : talent){
                    if(talentPVo.talentID != skillID)continue;
                    dragonTalentPVo = talentPVo;
                    level = dragonTalentPVo.level;
                    break;
                }
            }
        }
        ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(skillID);
        if(skillList==null)return;
        if(level>=skillList.size()-1)return;
        SkillBaseVo learnVo=skillList.get(level);

        if( user.costUserDataAndPropList(learnVo.costUserdata, true, ReasonTypeEnum.DRAGON_SKILL_UP, null)==false)return;
        level++;
        if(isGameBreaker){
            dragonPVo.skillLevel = level;
        }else if(dragonTalentPVo != null){
            dragonTalentPVo.level = level;
        }else{
            dragonTalentPVo = new DragonTalentPVo();
            dragonTalentPVo.index = -1;
            dragonTalentPVo.talentID = skillID;
            dragonTalentPVo.level = level;
            if(talent == null){
                talent = new ArrayList<>();
                talent.add(dragonTalentPVo);
                user.dragonModel.dragonTalentMap.put(dragonPVo.baseID,talent);
            }else{
                talent.add(dragonTalentPVo);
            }

        }
        new DragonSkillUpRspd(client,rqst.baseID,rqst.skillID,level);
//        new GeneralSuccessRspd(client,baseRqst.protocolID);
        if(isGameBreaker){
            user.cacheUserVo.dragonCacheModel.saveSqlData();
        }else{
            user.dragonModel.saveSqlData();
        }
    }
}
