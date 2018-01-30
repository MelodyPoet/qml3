package m1bhtree.action;

import m1bhtree.base.BaseNode;
import m1bhtree.base.basetype.RandomFloat;

public class Cooldown extends BaseNode {
    public RandomFloat time;
    public boolean runFirstTime=false;
    double lastTrueTime=0;
    public   boolean execute ()
    {

        super.execute();

        if (runFirstTime==false&&lastTrueTime==0) {
            lastTrueTime=System.currentTimeMillis()*0.001;
        }
        if(System.currentTimeMillis()*0.001-lastTrueTime>time.getRemain()){
            lastTrueTime=System.currentTimeMillis()*0.001;
            time.clear();
            return true;
        }
        return false;

    }
}
