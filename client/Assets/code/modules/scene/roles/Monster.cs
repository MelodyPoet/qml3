using System;
using modules.scene.model;
using starbucks.basic;
using starbucks.utils;
using UnityEngine;

namespace modules.scene.roles
{
    public class Monster : MonoBehaviour
    {
      
 

        public void loadres(Action onLoad=null)
        {
            transform.localScale= Vector3.one*0.6f;
           GlobalCoroutine.instance.StartCoroutine( AssetBundleManager.load("npc/monster.abd",null, (ab) =>
           {
               GlobalCoroutine.instance.StartCoroutine(AssetBundleManager.load(
                   "npc/" + BaseData.NpcBaseMap[SceneModel.instance.currentNpcLayout.npcID].image + ".abd",null,
                   (ab2) =>
                   {
                       GameObject res= Instantiate( ab.LoadAsset<GameObject>("monster"));
                       res.transform.SetParent(transform);
                       TransformUtils.resetMatrix(res.transform);
                       GetComponentInChildren<MovieClip2>().spritesAsset = ab2.LoadAllAssets<MovieClipAsset>()[0];
                       if (onLoad!=null) onLoad();
                   }));
        
            }));
        }
    }
}