 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using modules.scene.model;
using modules.scene.roles;
using starbucks.basic;
using UnityEngine.UI;
 
using starbucks.ui.basic;
using starbucks.utils;

namespace  modules.scene.main
{
    public class GameScene: MonoBehaviour
    {
        private GameObject currentScene;
        public static GameScene instance
        {
            get;
            private set;
        }

        private void Awake()
        {
            instance = this;
            EventDispatcher.globalDispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);
            EventDispatcher.globalDispatcher.AddEventListener(SceneFindMonsterRspd.PRO_ID,onSceneFindMonsterRspd);
            EventDispatcher.globalDispatcher.AddEventListener(SceneEvent.EVENT_SCENE_DIE,onSceneDie);
        }

        private void onSceneDie(EventData obj)
        {
         GlobalCoroutine.instance.delayApply(1,() =>
             {
                 new SceneFindMonsterRqst().send();
             }
         );
        }

        private void onSceneEnterRspd(EventData eventData)
        {
            loadScene();
        }
     private void onSceneFindMonsterRspd(EventData eventData)
     {
         Monster mst=    new GameObject("mst").AddComponent<Monster>();
         mst.loadres(()=>{
             mst.transform.position=currentScene.transform.Find("posList").GetChild(1).position;

             gameObject.AddComponent<Test>();
             Test.instance.currentMonster = mst.GetComponentInChildren<MovieClip2>();
             Test.instance.hero=MyHero.instance.GetComponentInChildren<MovieClip2>();
         });

     }
        private void loadScene()
        {
            if (currentScene != null)
            {
                Destroy(currentScene);
            }
            GlobalCoroutine.instance.StartCoroutine(  AssetBundleManager.load("map/"+SceneModel.instance.currentMap.model + ".abd",null, (ab) =>
            {
              currentScene= Instantiate( ab.LoadAsset<GameObject>(SceneModel.instance.currentMap.model));
                MyHero.instance.transform.position = currentScene.transform.Find("posList").GetChild(0).position;
                if (SceneModel.instance.currentMap.type == MapTypeEnum.NORMAL)
                {
                    new SceneFindMonsterRqst().send();
                }
            }));
            if (MyHero.instance == null)
            {
                new GameObject("myHero").AddComponent<MyHero>();
                MyHero.instance.loadres();
            }
        }
    }

}