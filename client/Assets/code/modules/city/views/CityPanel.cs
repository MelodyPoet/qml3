 
using starbucks.ui.basic;

namespace modules.cityMainPage.views
{
    public class CityPanel: BasePanel<CityModule>
    {
        public override void Init()
        {
            base.Init();
            createView<CityView>(gameObject);
            createView<CityMyHeadView>(transform.Find("myHead").gameObject);
            
 
        }
    }
}