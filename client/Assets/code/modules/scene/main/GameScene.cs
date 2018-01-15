 
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
            AssetBundleMgr.loadAndDestroy("map/map_city.abd", (ab) =>
            {
              currentScene= Instantiate( ab.LoadAsset<GameObject>("map_city")); 
            });
            if (MyHero.instance == null)
            {
                new GameObject("myHero").AddComponent<MyHero>();
                MyHero.instance.loadres();
            }
        }
    }

}