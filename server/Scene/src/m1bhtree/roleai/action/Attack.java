package m1bhtree.roleai.action;

import gluffy.udp.core.BaseRspd;
import gluffy.utils.JkTools;
import m1bhtree.base.BaseNode;
import m1bhtree.base.basetype.DirEnum;
import m1bhtree.base.basetype.RandomFloat;
import navigation.AStar;
import navigation.Vector2;
import protocol.MltSceneHurtRspd;
import sceneRole.BaseRole;

public class Attack extends BaseNode {


    public   boolean execute()
    {
        super.execute();

role.gameRole.attackCtrl.attack();
        return true;

    }
}
