package m1bhtree.action;

import m1bhtree.base.BaseNode;
import m1bhtree.base.basetype.RandomFloat;

public class GlobalDelay extends BaseNode {
    public RandomFloat rndTime;

    public   boolean execute ()
    {
        super.execute ();
        airoot.globalDelay = rndTime.getValue()+System.currentTimeMillis()*0.001;
        return true;

    }
}
