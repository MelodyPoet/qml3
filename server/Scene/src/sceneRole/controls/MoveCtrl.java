package sceneRole.controls;

import gluffy.udp.core.BaseRspd;
import navigation.AStar;
import navigation.Vector2;
import protocol.MltSceneMoveToRspd;

import sceneRole.BaseRole;

import java.util.ArrayList;

public class MoveCtrl {
    BaseRole selfRole;
    public boolean running;
 private    ArrayList<Vector2> currentPath;
    private   Vector2 targetPos=new Vector2();
    public MoveCtrl(BaseRole selfRole) {
       this.selfRole=selfRole;
    }
    public void loop(){
        if(running==false)return;

        Vector2 myPos=new Vector2( selfRole.posx* AStar.scale/ 10000f,selfRole.posz* AStar.scale/ 10000f);
        ArrayList<Vector2> path=currentPath;

        if(currentPath==null||currentPath.size()==0) {
            currentPath= path= selfRole.scene.findPath(myPos,targetPos);
        }

        if(path==null|| path.size()==0){
       stop();
            return;
        }
        Vector2 tgPos=path.get(0);



        selfRole.dir=(short)(9000-Math.atan2(tgPos.y-selfRole.posz*0.001f,tgPos.x-selfRole.posx*0.001f)*180/Math.PI*100);
        Vector2 newPos=new Vector2();
        Vector2.MoveTowards(myPos,tgPos,selfRole.moveSpeed* AStar.scale*0.002f,newPos);
        if(newPos.equalsWith(tgPos)){
            path.remove(0);
            if(path.size()==0){
                stop();
                return;
            }
        }
        selfRole. posx=(int)(newPos.x*10000/AStar.scale);
        selfRole. posz=(int)(newPos.y*10000/AStar.scale);
        selfRole.onPosChanged();
      //  System.out.println("moveTime="+selfRole. posx+","+selfRole.posz);
        BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
        new MltSceneMoveToRspd(null,selfRole.tempID,selfRole.posx,selfRole.posz, selfRole.dir);


    }

    public  void  stop(){
        if(running==false)return;
        running=false;
     //   if(cast) {
          //  BaseRspd.tempCast(selfRole.currentCell.getCastTargetsNear(0));
        //    new MltSceneMoveToRspd(null, selfRole.tempID, selfRole.posx, selfRole.posz, selfRole.dir, (short) 1, 0);
     //   }
        targetPos.clear();
        currentPath=null;
    }

    public void start(float x, float y) {
        targetPos.set(x,y);
        Vector2 myPos=new Vector2( selfRole.posx* AStar.scale/ 10000f,selfRole.posz* AStar.scale/ 10000f);
if(Vector2.Distance(myPos,targetPos)>10* AStar.scale){
         //   currentPath = selfRole.scene.findPath(myPos, targetPos);
        }
        running=true;
    }
}
