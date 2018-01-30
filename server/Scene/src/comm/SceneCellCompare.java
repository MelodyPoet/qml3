package comm;

import sceneRole.Hero;
import sceneRole.Npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SceneCellCompare {
    public   ArrayList<Long> newClients=new ArrayList<>();
    public ArrayList<Long> sameClients=new ArrayList<>();
    public ArrayList<Long> removeClients=new ArrayList<>();
    public ArrayList<Npc> newNpcs=new ArrayList<>();
    public ArrayList<Hero> newClientsForVo=new ArrayList<>();
    public ArrayList<Long> removeClientsAndNpcs = new ArrayList<>();
    public  SceneCellCompare(SceneCell oldCell ,SceneCell newCell,boolean calculateCastTargets,boolean calculateRoles,long clientGuid) {
            Scene scene=newCell.scene;
        HashMap<SceneCell,Integer> cells= getCellsState(oldCell,newCell);


        for (Map.Entry<SceneCell, Integer> cell : cells.entrySet()) {
            if (cell.getValue() == 2) {
                if(calculateCastTargets) {
                    newClients.addAll(cell.getKey().getCastTargets(clientGuid));
                }
                if(calculateRoles) {
                    if (cell.getKey().allHeros != null) {
                        newClientsForVo.addAll(cell.getKey().allHeros);
                    }
                    if (cell.getKey().allNpcs != null && cell.getKey().allNpcs.size() > 0) {
                        newNpcs.addAll(cell.getKey().allNpcs);
                    }
                }
            } else if (cell.getValue() == 1) {
              //System.out.println(cell.getKey().cellX + "," + cell.getKey().cellY);
                if(calculateCastTargets) {
                    removeClients.addAll(cell.getKey().getCastTargets(clientGuid));
                }
                if(calculateRoles) {
                    removeClientsAndNpcs.addAll(cell.getKey().getTargets(true, true, clientGuid));
                }
            }
        }
    }
    public HashMap<SceneCell,Integer> getCellsState(SceneCell oldCell, SceneCell newCell){
        HashMap<SceneCell,Integer> dic=new HashMap<>();
        Scene scene=newCell.scene;
        int halfCount=(scene.CastCells-1)/2;
        if(oldCell!=null) {
            for (int j = oldCell.cellX - halfCount; j <= oldCell.cellX + halfCount; j++) {
                if (j < 0 || j >= scene.cellColumn) continue;

                for (int i = oldCell.cellY - halfCount; i <= oldCell.cellY + halfCount; i++) {
                    if (i < 0 || i >= scene.cellRow) continue;
                    SceneCell cell =scene.allCells[j][i];
                    if (cell != null) {
                        dic.put(cell, 1);
                    }

                }
            }
        }
        if(newCell!=null){
            for (int j = newCell.cellX-halfCount; j <=newCell.cellX+halfCount ; j++) {
                if (j < 0 || j >= scene.cellColumn) continue;

                for (int i = newCell.cellY - halfCount; i <= newCell.cellY + halfCount; i++) {
                    if (i < 0 || i >= scene.cellRow) continue;
                    SceneCell cell = scene.allCells[j][i];
                    if (cell != null) {
                        if (dic.containsKey(cell)) {
                            dic.put(cell, 3);
                        } else {
                            dic.put(cell, 2);
                        }

                    }
                }

            }
        }

        return  dic;

    }
}
