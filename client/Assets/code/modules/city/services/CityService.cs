using System.Collections;
using System.Collections.Generic;
 using modules.city;
using starbucks.basic;
using starbucks.ui;
using UnityEngine;
using starbucks.ui.basic;
 namespace modules.cityMainPage.services
{
    public class CityService:BaseService
    {
      private CityModel model
        {
            get { return ModulesManager.city.model; }
        }
        
        public override void init()
        {
            base.init();
            dispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);

        }
        private void onSceneEnterRspd(EventData eventData)
        {
            SceneEnterRspd rspd= eventData.data as SceneEnterRspd;
            if (rspd.sceneID == 1)
            {
                new ShowViewCmd(ModuleEnum.CityMainPage).excute();
            }
            else
            {
                new HideViewCmd(ModuleEnum.CityMainPage).excute();
            }
        }
         
    }
}
