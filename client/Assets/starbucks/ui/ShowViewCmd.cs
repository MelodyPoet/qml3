using starbucks.basic;

namespace starbucks.ui
{
    public class ShowViewCmd  : ICommand 
    {
        private int moduleID;
   

        public ShowViewCmd(int moduleID)
        {
            this.moduleID = moduleID;
        }

        public void excute()
        {
        starbucks.basic.EventDispatcher.globalDispatcher.DispatchEvent(ModuleEvent.SHOW_MAIN_VIEW, moduleID);
        }
    }

}