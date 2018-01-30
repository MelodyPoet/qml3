using starbucks.basic;
using UnityEngine;

namespace starbucks.ui
{
    public class ShowViewCmd  : ICommand 
    {
        private int moduleID;
        private string tabName;

        public ShowViewCmd(int moduleID,string tabName=null)
        {
            Debug.LogError(moduleID+":");
            this.moduleID = moduleID;
            this.tabName = tabName;
        }

        public void excute()
        {
            EventData eventData=new EventData();
            eventData.intVal = moduleID;
            eventData.strVal = tabName;
            eventData.eventType = ModuleEvent.SHOW_MAIN_VIEW;
       EventDispatcher.globalDispatcher.DispatchEvent(eventData);
        }
    }

}