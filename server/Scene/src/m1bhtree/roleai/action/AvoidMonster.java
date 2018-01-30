package m1bhtree.roleai.action;

import m1bhtree.base.BaseNode;
import sceneRole.BaseRole;
import sceneRole.Npc;

public class AvoidMonster extends BaseNode{
    public float dis=1;
    public   boolean execute ()
    {
        super.execute ();
        float disCheck=dis*dis;
if(role.gameRole.currentCell.allNpcs==null) return false;
        for (BaseRole m :role.gameRole.currentCell.allNpcs) {
        if(m==role.gameRole)continue;
if(m==null||role.getTarget()==null)continue;
        if(role.gameRole.distanceSqar(m)>disCheck)continue;
        if(role.getTarget().distanceSqar(m)>role.getTarget().distanceSqar(role.gameRole))continue;
        return true;
    }

        return false;
    }
}
