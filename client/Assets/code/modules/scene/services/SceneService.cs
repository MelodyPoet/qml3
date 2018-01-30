using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;
using starbucks.ui.basic;
 namespace modules.scene.services
{
    public class SceneService:BaseService
    {
        private SceneModel model
        {
            get { return _baseModel as SceneModel; }
        }
         public override void init()
        {
            base.init();
            dispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);
            dispatcher.AddEventListener(SceneFindMonsterRspd.PRO_ID,onSceneFindMonsterRspd);
        }

        private void onSceneFindMonsterRspd(EventData eventData)
        {
             SceneFindMonsterRspd rspd = eventData.data as SceneFindMonsterRspd;
            model.currentNpcLayout = BaseData.NpcLayoutBaseMap[model.currentMap.ID][rspd.npcLayoutIndex];
      
        
        }


        private void onSceneEnterRspd(EventData eventData)
        {
            SceneEnterRspd rspd = eventData.data as SceneEnterRspd;
            model.currentMap = BaseData.MapBaseMap[rspd.sceneID];
            
        }

    }
}
