using System.Collections;
using System.Collections.Generic;
 
using modules.scene;
 
using starbucks.basic;
using starbucks.ui.basic;
using UnityEngine;
using UnityEngine.UI;

namespace modules.battleMainPage.views
{



public class BattleMonsterHeadView : BaseView<BattleModule, BattlePanel> {
    private SceneModel sceneModel
    {
        get { return ModulesManager.scene.model; }
    }
    public override void Awake()
    {
        base.Awake();
 
        
        updateName();
   
        dispatcher.AddEventListener(SceneFindMonsterRspd.PRO_ID,onSceneFindMonsterRspd);
        dispatcher.AddEventListener(SceneEvent.EVENT_SCENE_HURT,onSceneHurt);
        dispatcher.AddEventListener(SceneEvent.EVENT_SCENE_DIE,onSceneDie);
    }

    private void onSceneHurt(EventData eventData)
    {
        if ((bool)eventData.aryVal[0]  == false)
        {
            transform.Find("barHp").GetComponent<Image>().fillAmount -= (int)eventData.aryVal[1] * 0.01f;
        }
    }
    private void onSceneDie(EventData eventData)
    {
        if (eventData.intVal  == 1)
        {
           gameObject.SetActive(false);
        }
    }

    private void onSceneFindMonsterRspd(EventData eventData)
    {
   
        updateName();


    }

    private void updateName()
    {
        if(sceneModel.currentNpcLayout==null)
        {
            gameObject.SetActive(false);
            return;
            
       } 
        gameObject.SetActive(true);
        transform.Find("barHp").GetComponent<Image>().fillAmount = 1;
        int npcID = sceneModel.currentNpcLayout.npcID;
        transform.Find("txtName").GetComponent<Text>().text =BaseData.NpcBaseMap[npcID].name;
    }
}
}