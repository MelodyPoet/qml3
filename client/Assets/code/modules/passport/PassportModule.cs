 
 using modules.passport.services;
using modules.passport.views;
using starbucks.ui.basic;
namespace modules.passport
{
    public   class PassportModule : BaseModule
    {
        public PassportModel model { get; private set; }
        public PassportService service { get; private set; }
        public override void init()
        {
           base.init();
            model=new PassportModel();
          
           service= RegService<PassportService>();
          RegPanel<PassportPanel>(ModuleEnum.PASSPORT,"passportMainView","passport","comm");          
        }

   
    }

}