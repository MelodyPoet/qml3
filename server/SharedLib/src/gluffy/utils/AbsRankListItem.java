package gluffy.utils;



/**
 * Created by jackie on 14-5-3.
 */
public abstract  class AbsRankListItem{
    public abstract int orderScore();
    public int orderIndex;
    public abstract void onRemove(byte type);

}