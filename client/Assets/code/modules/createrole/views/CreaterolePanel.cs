 
using starbucks.ui.basic;

namespace modules.createrole.views
{
    public class CreaterolePanel: BasePanel<CreateroleModule>
    {
        public override void Init()
        {
            base.Init();
            createView<CreateroleView>(gameObject);
        }
    }
}