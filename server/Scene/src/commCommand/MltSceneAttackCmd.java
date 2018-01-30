package commCommand;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.SceneCell;
import gluffy.comm.BaseRqst;
import gluffy.udp.core.BaseRspd;
import protocol.MltSceneAttackRqst;
import protocol.MltSceneAttackRspd;

import sceneRole.Hero;
import table.SkillBaseVo;

public class MltSceneAttackCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, BaseRqst baseRqst) {
        MltSceneAttackRqst rqst = (MltSceneAttackRqst) baseRqst;

     Hero hero= client.hero;
     hero.dir=rqst.dir;
      SceneCell cell=  hero.scene.getCellByPos(hero.posx,hero.posz,true);
     SkillBaseVo skillBaseVo= Model.SkillBaseMap.get((int)rqst.skillID).get((int)rqst.skillLevel-1);
     hero.currentSkill=skillBaseVo;
      if(cell==null)return;
        BaseRspd.tempCast(hero.currentCell.getCastTargetsNear(client.guid));
        new MltSceneAttackRspd(null,hero.tempID,rqst.dir,rqst.skillID,rqst.skillLevel);

    }
}
