 
using a1.shared;
using modules.battleMainPage;
 
using modules.cityMainPage;
using modules.createrole;
using modules.passport;
using modules.scene.model;
using starbucks.basic;


public class ModulesManager
    {
 
    public static void init()
    {
        EventDispatcher.globalDispatcher.transmitDispatcher = UIEventDispatcher.globalUIDispatcher;
            new PassportModule().init();
            new CityModule().init();
            new CreateroleModule().init();
            new BattleModule().init();
            //场景模块 特殊初始化
            SceneModel.instance.service=new SceneService();
            SceneModel.instance.service.init();
        //    starbucks.basic.EventDispatcher.globalDispatcher.AddEventListenerOnce(GoMapRspd.PRO_ID,onInitOnce);
    }
        private static void onInitOnce(EventData eventData)
        {
            

    } 

  
    }
 