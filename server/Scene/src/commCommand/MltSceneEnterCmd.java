package commCommand;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Scene;
import comm.SceneCell;
import gluffy.comm.BaseRqst;
import gluffy.udp.core.BaseRspd;
import gluffy.utils.JkTools;
import protocol.*;
import qmshared.Model;
import sceneRole.Hero;
import sceneRole.Npc;
import table.AttributeEnum;

import java.util.ArrayList;

public class MltSceneEnterCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, BaseRqst baseRqst) {
        MltSceneEnterRqst rqst = (MltSceneEnterRqst) baseRqst;
if(client.hero==null||client.hero.baseID!=rqst.user.baseID){
    if( client.hero!=null){
        if (client.hero.scene != null) {
            client.hero.scene.exit(client.hero);
        }
    }
    client.hero=new Hero(client,rqst.user);
    client.hero.hp=((ArrayList<Integer>)rqst.userAttributes).get(AttributeEnum.HP_MAX);
}
Hero hero=client.hero;
        if (hero.scene != null) {
            hero.scene.exit(hero);
        }
        hero.baseAttributes=JkTools.intListAsArray((ArrayList<Integer>)rqst.userAttributes);

            Scene scene = Scene.getOrCreateNewScene(rqst.mapID , 10);


           // client.user.tempID = client.guid;
//            if(rqst.mapID==998) {
//                herp.posx = -440000;
//                herp.posz = -660000;
//            }if(rqst.mapID==1){
//            herp.posx = -60000;
//            herp.posz = -1670000;
//        }
        hero.posx=scene.mapBaseVo.enterPos[0]*1000;
        hero.posz=scene.mapBaseVo.enterPos[1]*1000;
        hero.dir=(short)(scene.mapBaseVo.enterPos[2]*100);
        SceneCell cell= scene.getCellByPos(hero.posx,hero.posz,true);


        new MltGoMapRspd(client, hero.posx, hero.posz, hero.dir);
        scene.enter(hero);





    }
}
