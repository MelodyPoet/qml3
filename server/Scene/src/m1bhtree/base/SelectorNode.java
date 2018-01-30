package m1bhtree.base;

public class SelectorNode extends BaseNode {




    @Override
    public boolean execute() {
        super.execute();
        for (BaseNode item : children) {
            if (airoot.breakOnce)
                break;
            if (item.enable == false) continue;
            if (item == null)
                continue;
            if (item.executeFinal() == true) return true;
        }
        return false;
    }
}
