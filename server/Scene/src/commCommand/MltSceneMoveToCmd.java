package commCommand;

import comm.BaseRqstCmd;
import comm.Client;
import comm.SceneCell;
import gluffy.comm.BaseRqst;
import gluffy.udp.core.BaseRspd;
import protocol.*;
import sceneRole.Hero;
import sceneRole.Npc;

public class MltSceneMoveToCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, BaseRqst baseRqst) {
        MltSceneMoveToRqst rqst = (MltSceneMoveToRqst) baseRqst;

     Hero hero= client.hero;
        hero.posx=rqst.posx;
        hero.posz=rqst.posz;
        hero.dir=rqst.dir;
      SceneCell cell=  hero.scene.getCellByPos(hero.posx,hero.posz,true);
      if(cell==null)return;
      if(cell!=hero.currentCell){
          cell.enterCell(hero);
      }


        BaseRspd.tempCast(hero.currentCell.getCastTargetsNear(client.guid));
        new MltSceneMoveToRspd(null,hero.tempID,hero.posx,hero.posz,hero.dir);

    }
}
