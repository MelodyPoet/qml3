 
 using modules.city;
 using modules.cityMainPage.services;
 using modules.cityMainPage.views;
using starbucks.ui.basic;
namespace modules.cityMainPage
{
    public   class CityModule : BaseModule
    {
          public CityModel model ;
 
        public override void init()
        {
           base.init();
            model=new CityModel();
            RegService<CityService>();
            
          RegPanel<CityPanel>(ModuleEnum.CityMainPage,"cityMainPageView","citymainpage","comm");
 
           
        }


 

    }

}