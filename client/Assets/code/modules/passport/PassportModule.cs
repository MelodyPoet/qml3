 
using modules.passport.model;
using modules.passport.views;
using starbucks.ui.basic;
namespace modules.passport
{
    public   class PassportModule : BaseModule
    {
          public PassportModel model = PassportModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new PassportService();
          RegPanel<PassportPanel>(ModuleEnum.PASSPORT,"passportMainView","passport","comm");          
        }


 

    }

}