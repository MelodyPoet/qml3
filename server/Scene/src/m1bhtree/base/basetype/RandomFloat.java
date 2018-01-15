package m1bhtree.base.basetype;

import gluffy.utils.JkTools;

public class RandomFloat {
    public float mix, max = -9999;
    private float lastRandom = -9999;

    public float getValue()
    {

            lastRandom = max == -9999 ? mix : JkTools.getRandBetweenf(mix, max);
            return lastRandom;

    }

    public void clear()
    {
        lastRandom = -9999;
    }

    public float getRemain()
    {
        if (lastRandom == -9999)
        {
            float a =	getValue();
        }
        return lastRandom;
    }

}
