package sceneRole;

import comm.Scene;
import comm.SceneCell;
import protocol.PropPVo;
import protocol.SkillPVo;
import sceneRole.controls.AttackCtrl;
import sceneRole.controls.MoveCtrl;

import java.util.ArrayList;
import java.util.Collection;

public class BaseRole {
    public long tempID;
    public int baseID;
    public byte level;
    public int zdl;
    public int posx;
    public int posz;
    public short dir;
    public Scene scene;
    public SceneCell currentCell;
    public int hp=500;
    public int[] baseAttributes;//buff以外人物固定属性
    public  int[]   dynamicAttributes;//buff算上动态属性
    public int moveSpeed=300;

    public float distanceSqar(BaseRole tg){
        return (tg.posx-posx)/10000f*(tg.posx-posx)/10000f+(tg.posz-posz)/10000f*(tg.posz-posz)/10000f;
    }
    public MoveCtrl moveCtrl=new MoveCtrl(this);
    public AttackCtrl attackCtrl=new AttackCtrl(this);

    public void lookAt(BaseRole target) {

       dir= (short)(9000-Math.atan2( target.posz-posz, target.posx-posx)*180/Math.PI*100);
    }

    public void clear() {
         moveCtrl.stop();
        attackCtrl.currentAttackTarget=null;
    }
   public void calculateBaseAttribute(){};
    public void calculateDynamicAttribute(){};

    public void onPosChanged() {
    }
}
