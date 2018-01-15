package m1bhtree.roleai.action;

import gluffy.utils.JkTools;
import m1bhtree.base.BaseNode;
import m1bhtree.base.basetype.DirEnum;
import m1bhtree.base.basetype.RandomFloat;
import navigation.AStar;
import navigation.Vector2;
import sceneRole.BaseRole;
import sceneRole.Npc;

public class SetRotation extends BaseNode {
    public byte dir;
    public RandomFloat randomRot;

    public int playAct = 0;
    public float lerp = 1.0f;

    public   boolean execute()
    {
        super.execute();
        int des = role.gameRole.dir;
        float dx=0,dy=0;
        BaseRole target=role.getTarget();
        switch (dir)
        {
            case  DirEnum.moveTo:
                if (role.gameRole == target)
                    break;
//                    if(role.gameRole.roleVo.type == MapItemEnum.USER)
//                        Debug.LogError("set  rotation   role target ==   " + role.target);
                dx= target.posx- role.gameRole.posx;
                dy= target.posz- role.gameRole.posz;
                break;
            case DirEnum.escape:
                if (role.gameRole ==  target)
                    break;
                dx=  role.gameRole.posx-target.posx;
                dy=  role.gameRole.posz-target.posz;
               float dis= Vector2.Magnitude(dx,dy);
                dx*=6/dis;
                dy*=6/dis;
                break;
            case DirEnum.toTargetPos:
            //    des = Quaternion.LookRotation(role.targetPos - role.transform.position);
                dx= role.targetPos.x- role.gameRole.posx;
                dy= role.targetPos.y- role.gameRole.posz;
                break;
            case DirEnum.toWalkCenter:
                //des = Quaternion.LookRotation(role.walkCenter - role.transform.position);

                break;
        }
   if(dx!=0||dy!=0){
       des= (short)(9000-Math.atan2(dy,dx)*180/Math.PI*100);
   }

        if (randomRot.max != -9999)
            des +=   randomRot.getValue() * (JkTools.getRandBetween(0, 100) < 50 ? -1 : 1)*100;
      //  if (playAct > 0)
         //   role.playAnm(playAct);
 //role.gameRole.dir=(short) des;
       role.gameRole.moveCtrl.start((role.gameRole.posx+dx)* AStar.scale/10000,(role.gameRole.posz+dy)* AStar.scale/10000);
        return true;

    }
}
