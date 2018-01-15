package flushnpc;

import comm.Model;
import comm.Scene;
import sceneRole.Npc;
import table.AttributeEnum;
import table.NpcLayoutBaseVo;
import util.Pair;

import java.util.ArrayList;

public class BaseFlushNpc {
 private Scene scene;
    private ArrayList<Pair<Long, Npc>> flushList=new ArrayList<>();

    public BaseFlushNpc(Scene scene) {
        this.scene=scene;
    }

    public void addOne(Npc npc){
        flushList.add(new Pair<>(System.currentTimeMillis()+5000,npc));
    }
    public void loop(){
        tryFlush();
    }

    private void tryFlush() {
        if(flushList.size()==0)return;
        if(System.currentTimeMillis()>flushList.get(0).getKey()){
          Npc npc=  flushList.get(0).getValue();
          npc.currentCell=null;
          npc.attackCtrl.currentAttackTarget=null;
npc.posx=npc.bornX;
            npc.posz=npc.bornZ;
            npc.dir=npc.bornDir;
           npc.hp= npc.baseAttributes[AttributeEnum.HP_MAX];
            npc.scene.allNpc.put(npc.tempID,npc);
            scene.enter(npc);
            flushList.remove(0);
            //tryFlush();
        }
    }

    public void start() {
        ArrayList<NpcLayoutBaseVo> npcLays = Model.NpcLayoutBaseMap.get(scene.mapBaseVo.ID);
        int tempID=-1;
        if(npcLays==null)return;
        for (NpcLayoutBaseVo layout:npcLays) {
            int[] ary = layout.pos;
            if(ary!=null) {

                for (int i = 0; i < ary.length/3; i++) {
                    tempID--;
                    Npc npc = new Npc(tempID,layout,scene);

                    npc.baseID =   layout.npcID;
                    npc.level = (byte) layout.level;
                    npc.posx =ary[i*3]*1000;
                    npc.posz = ary[i*3+1]*1000;
                    npc.dir=(short)(ary[i*3+2]*100);
                    npc.calculateBaseAttribute();
                    npc.hp= npc.baseAttributes[AttributeEnum.HP_MAX];
                    scene.enter(npc);

                }
            }
        }
    }
}
