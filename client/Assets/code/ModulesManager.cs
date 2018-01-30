 
using a1.shared;
using modules.battleMainPage;
 
using modules.cityMainPage;
using modules.createrole;
using modules.passport;
using modules.scene;
 
using starbucks.basic;


public class ModulesManager
{
    public static PassportModule passport;
    public static CityModule city;
    public static CreateroleModule createrole;
    public static BattleModule battle;
    public static SceneModule scene;
    public static void init()
    {
        EventDispatcher.globalDispatcher.transmitDispatcher = UIEventDispatcher.globalUIDispatcher;
        passport= new PassportModule();
        
        city=   new CityModule();
        createrole=    new CreateroleModule();
        battle=  new BattleModule();
        scene = new SceneModule();
        /*  //场景模块 特殊初始化
          SceneModel.instance.service=new SceneService();
      SceneModel.instance.service.init();*/
        //    starbucks.basic.EventDispatcher.globalDispatcher.AddEventListenerOnce(GoMapRspd.PRO_ID,onInitOnce);
    }
        private static void onInitOnce(EventData eventData)
        {
            

    } 

  
    }
 