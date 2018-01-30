using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.ui;
using UnityEngine;
using starbucks.ui.basic;
 namespace modules.battleMainPage.model
{
    public class BattleService:BaseService
    {
  
		 private BattleModel model =BattleModel.instance;
        public override void init()
        {
            base.init();
            dispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);

        }
        private void onSceneEnterRspd(EventData eventData)
        {
            SceneEnterRspd rspd= eventData.data as SceneEnterRspd;
            if (rspd.sceneID != 1)
            {
                new ShowViewCmd(ModuleEnum.BattleMainPage).excute();
            }
            else
            {
                new HideViewCmd(ModuleEnum.BattleMainPage).excute();

            }
        }
         
    }
}
