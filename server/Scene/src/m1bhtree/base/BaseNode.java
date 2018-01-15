package m1bhtree.base;

import m1bhtree.base.basetype.FinalResultEnum;

import java.util.ArrayList;

public class BaseNode {
    public FinalResultEnum finalDeal = FinalResultEnum.nothing;
    protected ArrayList<BaseNode> children = new ArrayList<BaseNode>();
    public AiRoot airoot;
    boolean lastRst;
    boolean enable = true;
    private int _depth=0;
public AiBaseRole role;

    public   void init(AiBaseRole role,int depth) {
       _depth=depth;
        airoot=this.role=role;
    }
    public boolean execute() {
    if(this==airoot.rootNode){
        System.out.println("--root");
    }else{
        String strDepth="--";
        for (int i = 0; i <_depth ; i++) {
            strDepth+="--";
        }
        System.out.println(strDepth+this.getClass().getSimpleName());
    }

        return false;
    }

    public boolean executeFinal() {
        lastRst = execute();
        if (finalDeal != FinalResultEnum.nothing) {
            if (finalDeal == FinalResultEnum.forceTrue) {
                lastRst = true;
            } else if (finalDeal == FinalResultEnum.forceFalse) {
                lastRst = false;
            } else {
                lastRst = !lastRst;
            }

        }
        return lastRst;
    }

    public int executeInt() {
        return 0;
    }

    public boolean getBoolParam(int index) {
        return false;
    }

    public void addChild(BaseNode child) {
        children.add(child);
        child.init(role,_depth+1);
    }
}
