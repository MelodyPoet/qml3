 
 using modules.createrole.views;
using starbucks.ui.basic;
namespace modules.createrole
{
    public   class CreateroleModule : BaseModule
    {
  
        public override void init()
        {
           base.init();
           RegPanel<CreaterolePanel>(ModuleEnum.Createrole,"createRoleView","createrole","comm");
 
           
        }


 

    }

}