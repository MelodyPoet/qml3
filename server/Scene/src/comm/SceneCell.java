package comm;

import gluffy.udp.core.BaseRspd;
import protocol.*;

import sceneRole.Hero;
import sceneRole.Npc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//将整个场景 按固定大小分成 n个cell
public class SceneCell {

    public   int cellX;
    public  int cellY;
    public Scene scene;
    public HashSet<Hero> allHeros;
    public HashSet<Npc> allNpcs;

    public SceneCell(int cellX, int cellY,Scene scene) {

        this.cellX = cellX;
        this.cellY = cellY;
        this.scene = scene;
    }

    public void enterCell(Hero hero){
 SceneCellCompare sceneCellCompare=new SceneCellCompare(hero.currentCell,this,true,true,hero.client.guid);
        ArrayList<Long> newClients = sceneCellCompare.newClients;
        ArrayList<Long> removeClients = sceneCellCompare.removeClients;
        ArrayList<Long> removeClientsAndNpcs = sceneCellCompare.removeClientsAndNpcs;
        ArrayList<Npc> newNpcs = sceneCellCompare.newNpcs;
        ArrayList<Hero> newClientsForVo = sceneCellCompare.newClientsForVo;
        //让新别人添加我
        if(newClients.size()>0) {
            BaseRspd.tempCast(newClients);
            new MltSceneAddHeroRspd(null, hero.makProtoVo());
        }



        if(allHeros==null)allHeros=new HashSet<>();
        allHeros.add(hero);

//有旧位置
if(hero.currentCell!=null){
    hero.currentCell.allHeros.remove(hero);
//让旧别人删除我
    if(removeClients.size()>0) {
        BaseRspd.tempCast(removeClients);
        new MltSceneDeleteRspd(null, hero.tempID);
    }
    //让我删除旧怪物+旧玩家
    if(removeClientsAndNpcs.size()>0)
   new MltSceneDeleteListRspd(hero.client ,removeClientsAndNpcs);

}
//让我添加新怪物
        if(newNpcs.size()>0) {
            Collection<NpcPVo> npcPVos = newNpcs.stream().map(p -> {
                return p.makProtoVo();
            }).collect(Collectors.toList());

            new MltSceneAddNpcListRspd(hero.client, npcPVos);
        }
//让我添加新别人
        if(newClientsForVo.size()>0) {
            Collection<SkillUserPVo> userPVos = newClientsForVo.stream().map(p -> {
                return p.makProtoVo();
            }).collect(Collectors.toList());
            new MltSceneAddHeroListRspd(hero.client, userPVos);
        }
        hero.currentCell=this;

    }
    public void enterCell(Npc npc){
        SceneCellCompare sceneCellCompare=new SceneCellCompare(npc.currentCell,this,true,false,0);
        ArrayList<Long> newClients = sceneCellCompare.newClients;
        ArrayList<Long> removeClients = sceneCellCompare.removeClients;
         //让新别人添加我
        if(newClients.size()>0) {
            BaseRspd.tempCast(newClients);
            new MltSceneAddNpcRspd(null, npc.makProtoVo());
        }
        if(allNpcs==null)allNpcs=new HashSet<>();
        allNpcs.add(npc);
//有旧位置
        if(npc.currentCell!=null){
            npc.currentCell.allNpcs.remove(npc);
//让旧别人删除我
            if(removeClients.size()>0) {
                BaseRspd.tempCast(removeClients);
                new MltSceneDeleteRspd(null, npc.tempID);
            }

        }
        npc.currentCell=this;

    }

//获取 广播对象
    public ArrayList<Long> getCastTargets(long exceptClientGuid) {
        ArrayList<Long> targets=new ArrayList<>();

        if(allHeros!=null) {
            for (Hero hero : allHeros) {
                if (exceptClientGuid != hero.client.guid) targets.add( hero.client.guid);
            }
        }
return  targets;
    }
    public ArrayList<Long> getCastTargetsNear(long exceptClientGuid) {
        ArrayList<Long> targets=new ArrayList<>();
        for (SceneCell cell:getNearCells()
             ) {
            if(cell.allHeros!=null) {
                for (Hero hero : cell.allHeros) {
                    if (exceptClientGuid != hero.client.guid) targets.add( hero.client.guid);
                }
            }


        }

        return targets;

    }
    public ArrayList<Long> getTargets(boolean containClient,boolean containNpc, long exceptID) {

        ArrayList<Long> targets=new ArrayList<>();

        if(allHeros!=null&&containClient) {
            for (Hero hero : allHeros) {
                if (exceptID != hero.tempID) targets.add(hero.tempID);
            }
        }
        if(allNpcs!=null&&containNpc) {
            for (Npc npc : allNpcs) {
                targets.add(npc.tempID);
            }
        }
        return  targets;
    }
    public ArrayList<SceneCell> getNearCells() {
        ArrayList<SceneCell> targets = new ArrayList<>();
        int halfCount=(scene.CastCells-1)/2;
        for (int j =   cellX-halfCount; j <=cellX+halfCount ; j++) {
            if (j < 0 || j >= scene.cellColumn) continue;

            for (int i = cellY - halfCount; i <= cellY + halfCount; i++) {
                if (i < 0 || i >= scene.cellRow) continue;
                SceneCell cell =scene.allCells[j][i];
                if (cell != null) {
targets.add(cell);


                }
                }
                }
                return targets;


    }
    public ArrayList<Npc> getNpcNear() {
        ArrayList<Npc> targets = new ArrayList<>();
        int halfCount=(scene.CastCells-1)/2;
        for (int j =   cellX-halfCount; j <=cellX+halfCount ; j++) {
            if (j < 0 || j >= scene.cellColumn) continue;

            for (int i = cellY - halfCount; i <= cellY + halfCount; i++) {
                if (i < 0 || i >= scene.cellRow) continue;
                SceneCell cell =scene.allCells[j][i];
                if (cell != null&&cell.allNpcs!=null) {

                    targets.addAll(cell.allNpcs);

                }
            }
        }
        return targets;


    }


    public void exit(Npc npc,boolean cast) {
        allNpcs.remove(npc);
        if(cast) {
            BaseRspd.tempCast(getCastTargetsNear(0));
            new MltSceneDeleteRspd(null, npc.tempID);
        }
    }
}

