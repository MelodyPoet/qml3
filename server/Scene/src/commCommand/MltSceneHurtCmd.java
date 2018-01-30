package commCommand;

import comm.BaseRqstCmd;
import comm.Client;
import gluffy.comm.BaseRqst;
import gluffy.udp.core.BaseRspd;
import protocol.MltSceneDieRspd;
import protocol.MltSceneHurtRqst;
import protocol.MltSceneHurtRspd;
import sceneRole.Hero;
import sceneRole.Npc;
import table.AttributeEnum;

public class MltSceneHurtCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, BaseRqst baseRqst) {
        MltSceneHurtRqst rqst = (MltSceneHurtRqst) baseRqst;

        Hero hero = client.hero;
        Npc npc =null;
        if(rqst.tempRoleID!=hero.tempID)
        npc=  hero.scene.allNpc.get(rqst.tempRoleID);
        else
            npc=  hero.scene.allNpc.get(rqst.attackRoleID);
if(npc==null)return;
        BaseRspd.tempCast(hero.currentCell.getCastTargetsNear(0));
        if(rqst.tempRoleID!=hero.tempID) {
            if( hero.currentSkill==null)return;
            int hurtHp=hero.baseAttributes[AttributeEnum.ATTACK]*hero.currentSkill.hert/1000;
            npc.hp -=hurtHp;
            if (npc.hp > 0) {
                new MltSceneHurtRspd(null, npc.tempID, hurtHp, npc.hp);
                npc.attackCtrl.currentAttackTarget = hero;

            } else {
                npc.scene.flushNpc.addOne(npc);
                npc.clear();
                new MltSceneDieRspd(null, npc.tempID, (short) 0, (short) 1);
                npc.currentCell.exit(npc, false);//死亡的删除不用广播 死亡客户端直接删除
            }
        }else{
            int hurtHp=npc.baseAttributes[AttributeEnum.ATTACK];
            hero.hp-=hurtHp;
           new MltSceneHurtRspd(null, hero.tempID, hurtHp, hero.hp);

        }
    }
};


