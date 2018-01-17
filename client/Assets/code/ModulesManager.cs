 
using a1.shared;
using modules.battleMainPage;
using modules.battleMainPage.main;
using modules.cityMainPage;
using modules.createrole;
using modules.passport;
using modules.scene.model;
using starbucks.basic;


public class ModulesManager
    {
 
    public static void init()
        {
            
            new PassportModule().init();
            new CityMainPageModule().init();
            new CreateroleModule().init();
            new BattleMainPageModule().init();
            //场景模块 特殊初始化
            SceneModel.instance.service=new SceneService();
            SceneModel.instance.service.init();
        //    starbucks.basic.EventDispatcher.globalDispatcher.AddEventListenerOnce(GoMapRspd.PRO_ID,onInitOnce);
    }
        private static void onInitOnce(EventData eventData)
        {
            

    } 

  
    }
 