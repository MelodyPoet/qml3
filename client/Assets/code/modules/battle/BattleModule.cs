 
using modules.battleMainPage.model;
using modules.battleMainPage.views;
using starbucks.ui.basic;
namespace modules.battleMainPage
{
    public   class BattleModule : BaseModule
    {
          public BattleModel model ;
 
        public override void init()
        {
           base.init();
            model=new BattleModel();
            RegService<BattleService>();
           RegPanel<BattlePanel>(ModuleEnum.BattleMainPage,"battleMainPageView","battlemainpage","comm","citymainpage");
 
           
        }


 

    }

}