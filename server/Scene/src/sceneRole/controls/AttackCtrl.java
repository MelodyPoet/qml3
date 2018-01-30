package sceneRole.controls;

import comm.Client;
import gluffy.udp.core.BaseRspd;
import gluffy.utils.JkTools;
import navigation.AStar;
import navigation.Vector2;
import protocol.MltSceneAttackRspd;
import protocol.MltSceneHurtRspd;

import sceneRole.BaseRole;
import sceneRole.Npc;

import java.util.ArrayList;

public class AttackCtrl {
     BaseRole selfRole;
    public BaseRole currentAttackTarget;
     long lastAttackTime=System.currentTimeMillis();
     public AttackCtrl(BaseRole role) {
        selfRole=role;
    }

   public void  attack(){
         selfRole.moveCtrl.stop();
         selfRole.lookAt(currentAttackTarget);
       BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));

       new MltSceneAttackRspd(null,selfRole.tempID, selfRole.dir,(short)  ((Npc) selfRole).baseVo.skills[0],(byte)1);


     //  BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
      // new MltSceneHurtRspd(null,currentAttackTarget.tempID,50,1000);
   }
     //   if(selfRole.currentCell==null)return;
      //  if(currentAttackTarget==null)return;
         //Vector2 targetPos=new Vector2(currentAttackTarget.posx* AStar.scale/ 10000f,currentAttackTarget.posz* AStar.scale/ 10000f);
//        if(Vector2.Distance(myPos,targetPos)<2* AStar.scale){
//            if(running){
//                running=false;
//                currentPath=null;
//                BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
//                new MltScenePosRspd(null,selfRole.tempID,selfRole.posx,selfRole.posz, selfRole.dir,(short)1,0);
//                lastAttackTime=System.currentTimeMillis()-2000;
//            }
//            if(System.currentTimeMillis()-lastAttackTime>3000){
//                BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
//                new MltScenePosRspd(null,selfRole.tempID,selfRole.posx,selfRole.posz, selfRole.dir,(short)11,0);
//                lastAttackTime=System.currentTimeMillis()+ JkTools.getRandBetween(0,1000);
//                BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
//                new MltSceneHurtRspd(null,currentAttackTarget.tempID,50,1000);
//            }
//            return;
//        }






}
