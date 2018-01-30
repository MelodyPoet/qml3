 
using modules.cityMainPage.model;
using modules.cityMainPage.views;
using starbucks.ui.basic;
namespace modules.cityMainPage
{
    public   class CityModule : BaseModule
    {
          public CityModel model = CityModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new CityService();
          RegPanel<CityPanel>(ModuleEnum.CityMainPage,"cityMainPageView","citymainpage","comm");
 
           
        }


 

    }

}