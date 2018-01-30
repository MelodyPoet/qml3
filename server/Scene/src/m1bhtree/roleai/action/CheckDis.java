package m1bhtree.roleai.action;

import m1bhtree.base.BaseNode;
import m1bhtree.base.basetype.CompareEnum;
import navigation.Vector2;
import sceneRole.BaseRole;

public class CheckDis extends BaseNode {
    public enum DisEnum
    {
        selfAndTarget,
        selfAndWalkCenter,
        selfAndTargetPos
    }

    public float dis;
    public byte compare;
    public DisEnum type = DisEnum.selfAndTarget;
    float realDis;

    public   boolean execute()
    {
        super.execute();
        realDis = 0;
        Vector2 dt=new Vector2();
        if (type == DisEnum.selfAndTarget)
        {
          BaseRole target= role.getTarget();
            if (target != null)
            {
                dt.x = (target.posx - role.gameRole.posx);
                dt.y = (target.posz - role.gameRole.posz);

            }
            else
            {

            }
        }
        else if (type == DisEnum.selfAndWalkCenter)
        {
          // dt = (role.walkCenter - role.transform.position);
           // dt.x = (target.posx - role.gameRole.posx);
        //   dt.y = (target.posz - role.gameRole.posz);
        }
        else
        {

            dt.x = (role.targetPos.x - role.gameRole.posx);
            dt.y = (role.targetPos.y - role.gameRole.posz);
        }

        realDis = dt.magnitude();
        switch (compare)
        {
            case CompareEnum.Bigger:
                return realDis > dis;
            case CompareEnum.Equal:
                return realDis == dis;
            case CompareEnum.Little:
                return realDis < dis;
        }
        return false;
    }



    public float getFloat()
    {
        return realDis;
    }


}
