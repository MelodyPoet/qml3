using starbucks.basic;

namespace starbucks.ui
{
    public class HideViewCmd : ICommand 
    {
        private int moduleID;
   

        public HideViewCmd(int moduleID)
        {
            this.moduleID = moduleID;
        }

        public void excute()
        {
        starbucks.basic.EventDispatcher.globalDispatcher.DispatchEvent(ModuleEvent.HIDE_MAIN_VIEW, moduleID);
        }
    }

}