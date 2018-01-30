package sceneRole;

import comm.Model;
import comm.Scene;
import comm.SceneCell;
import m1bhtree.base.AiBaseRole;
import protocol.NpcPVo;
import sceneRole.controls.AttackCtrl;
import table.AttributeEnum;
import table.NpcBaseVo;
import table.NpcLayoutBaseVo;
import table.NpcLevelBaseVo;

public class Npc extends BaseRole {
    NpcPVo protoVo=new NpcPVo();
 public AiBaseRole aiBaseRole=null;
 private  NpcLayoutBaseVo layout;
 public NpcBaseVo baseVo;
    public int bornX;
    public int bornZ;
    public short bornDir;
    public  Npc(long tempID, NpcLayoutBaseVo layout, Scene scene){
        this.tempID=tempID;
        this.layout=layout;
        this.scene=scene;
        scene.allNpc.put(tempID,this);
    aiBaseRole=new AiBaseRole();
        baseVo=Model.NpcBaseMap.get( layout.npcID);
    aiBaseRole.init(null,this);
}
    public NpcPVo makProtoVo(){
        protoVo.baseID=baseID;
        protoVo.tempRoleID=tempID;
        protoVo.hpMax=baseAttributes[AttributeEnum.HP_MAX];
        protoVo.posx=posx;
        protoVo.posz=posz;
        protoVo.dir=dir;
        return protoVo;
    }
    public void  loopAct(){
        if(attackCtrl!=null&&attackCtrl.currentAttackTarget!=null) {
            if(attackCtrl.currentAttackTarget.scene!=scene){
                attackCtrl.currentAttackTarget=null;
                return;
            }
            aiBaseRole.Update();
            moveCtrl.loop();
         //  simpleNpcAttackCtrl.loop();
        }
       // currentCell.getCastTargetsNear(0);


    }

    @Override
    public void clear() {
        scene.allNpc.remove(tempID);
        super.clear();

    }

    @Override
    public void calculateBaseAttribute() {
        super.calculateBaseAttribute();
        NpcLevelBaseVo lvlVo=	Model.NpcLevelBaseMap.get(layout.level);
        int[] baseAtt = layout.attributes;
        if (baseAtt == null) {
            baseAttributes = RoleAttributeUtils.getAttrs(lvlVo.Properties);

            int []attributesPowers=	Model.NpcAttributeBaseMap.get(layout.attributeType).attributesPower;

            for (int i = 0; i < attributesPowers.length; i+=2) {
                baseAttributes [attributesPowers [i]] *= attributesPowers [i + 1];
                baseAttributes [attributesPowers [i]] /= 100;
            }

        }else{
            baseAttributes =RoleAttributeUtils.getAttrs(baseAtt);
        }
        moveSpeed=baseVo.moveSpeed;
    }

    @Override
    public void onPosChanged() {
        super.onPosChanged();
        SceneCell cell=  scene.getCellByPos(posx,posz,true);
        if(cell==null)return;
        if(cell!=currentCell){
            cell.enterCell(this);
        }
    }
}
