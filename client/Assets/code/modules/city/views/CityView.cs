 
using modules.cityMainPage.model;
using starbucks.basic;
using starbucks.uguihelp;
using starbucks.ui;
 
 
using starbucks.ui.basic;
namespace  modules.cityMainPage.views
{
    public class CityView: BaseView<CityModule,CityPanel>
    {
        public CityModel model = CityModel.instance;

        public override void Awake()
        {
            base.Awake();
     
            UIEventListener.Get(transform.Find("btnMap1").gameObject).onClick = (go) => { RequestEnterScene(2); };
             UIEventListener.Get(transform.Find("btnMap2").gameObject).onClick = (go) => { RequestEnterScene(4); };
            UIEventListener.Get(transform.Find("btnMap3").gameObject).onClick = (go) => { RequestEnterScene(35); };
            
        }

        private void RequestEnterScene(int mapid)
        {
            new SceneEnterRqst(mapid).send();
        }
  
        
    }

}