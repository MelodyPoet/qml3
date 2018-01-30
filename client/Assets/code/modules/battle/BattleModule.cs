 
using modules.battleMainPage.model;
using modules.battleMainPage.views;
using starbucks.ui.basic;
namespace modules.battleMainPage
{
    public   class BattleModule : BaseModule
    {
          private BattleModel model = BattleModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new BattleService();
           RegPanel<BattlePanel>(ModuleEnum.BattleMainPage,"battleMainPageView","battlemainpage","comm","citymainpage");
 
           
        }


 

    }

}