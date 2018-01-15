 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using modules.scene.roles;
using starbucks.basic;
using UnityEngine.UI;
 
using starbucks.ui.basic;
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
        }

        public void loadScene()
        {
          GlobalCoroutine.instance.StartCoroutine(  AssetBundleManager.load("map/map_city.abd",null, (ab) =>
            {
              currentScene= Instantiate( ab.LoadAsset<GameObject>("map_city")); 
            }));
            if (MyHero.instance == null)
            {
                new GameObject("myHero").AddComponent<MyHero>();
                MyHero.instance.loadres();
            }
        }
    }

}