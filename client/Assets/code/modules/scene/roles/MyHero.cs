using starbucks.basic;
using starbucks.utils;
using UnityEngine;

namespace modules.scene.roles
{
    public class MyHero : MonoBehaviour
    {
        public static MyHero instance
        {
            get;
            private set;
        }

        private void Awake()
        {
            instance = this;
        }

        public void loadres()
        {
            AssetBundleMgr.loadAndDestroy("npc/hero.abd", (ab) =>
            {
               GameObject res= Instantiate( ab.LoadAsset<GameObject>("hero"));
                res.transform.SetParent(transform);
                TransformUtils.resetMatrix(res.transform);
            });
        }
    }
}