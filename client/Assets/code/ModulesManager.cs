 
using a1.shared;
using modules.passport;
using starbucks.basic;


public class ModulesManager
    {
 
    public static void init()
        {
            
new PassportModule().init();
       
        //    starbucks.basic.EventDispatcher.globalDispatcher.AddEventListenerOnce(GoMapRspd.PRO_ID,onInitOnce);
    }
        private static void onInitOnce(EventData eventData)
        {
            

    } 

  
    }
 