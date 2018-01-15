package m1bhtree.base;

public class SequenceNode extends BaseNode {
    @Override
    public boolean execute() {

        super.execute();
        for (BaseNode item : children) {
            if (airoot.breakOnce)
                break;
            if (item.enable == false) continue;

            if (item.executeFinal() == false) {

                return false;
            }
        }
        return true;
    }

}
