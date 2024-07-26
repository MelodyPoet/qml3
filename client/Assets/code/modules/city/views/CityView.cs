 
 using modules.city;
 using starbucks.basic;
using starbucks.uguihelp;
using starbucks.ui;
 
 
using starbucks.ui.basic;
namespace  modules.cityMainPage.views
{
    public class CityView: BaseView<CityModule,CityPanel>
    {
        private CityModel model
        {
            get { return module.model; }
        }

        public override void Awake()
        {
            base.Awake();
     
            UIEventListener.Get(transform.Find("btnMap1").gameObject).onClick = (go) => { RequestEnterScene(2); };
             UIEventListener.Get(transform.Find("btnMap2").gameObject).onClick = (go) => { RequestEnterScene(4); };
            UIEventListener.Get(transform.Find("btnMap3").gameObject).onClick = (go) => { RequestEnterScene(35); };
            UIEventListener.Get(transform.Find("btnMap4").gameObject).onClick = (go) => { RequestEnterScene(21); };
            
        }

        private void RequestEnterScene(int mapid)
        {
            new SceneEnterRqst(mapid).send();
        }
  
        
    }

}