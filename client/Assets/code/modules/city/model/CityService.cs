using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.ui;
using UnityEngine;
using starbucks.ui.basic;
 namespace modules.cityMainPage.model
{
    public class CityService:BaseService
    {
  
		 private CityModel model =CityModel.instance;
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
