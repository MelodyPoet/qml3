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
           GlobalCoroutine.instance.StartCoroutine( AssetBundleManager.load("npc/hero.abd",null, (ab) =>
            {
               GameObject res= Instantiate( ab.LoadAsset<GameObject>("hero"));
                res.transform.SetParent(transform);
                TransformUtils.resetMatrix(res.transform);
            }));
        }
    }
}