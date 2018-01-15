using System;
using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

namespace starbucks.ui
{
	public class UIAssetBundleManager 
	{
		static Dictionary<string,AssetBundleCache> allitems=new  Dictionary<string, AssetBundleCache>();
		public	static	AssetBundle getOne (string key)
		{
			AssetBundleCache abc = getOneCache(key);
			return abc==null ? null : abc.assetBundle;
		}
		private	static	AssetBundleCache getOneCache (string key)
		{
			if (allitems.ContainsKey(key) == false) return null;
			return allitems [key];
		}
		public	static	Sprite getSprite(string key,string spriteName){
			return	allitems [key].assetBundle.LoadAsset<Sprite> (spriteName);
		}

		public static IEnumerator load(string resName, Action<AssetBundle> onLoad=null)
		{
			AssetBundleCache abc;
			if (allitems.TryGetValue(resName,out abc) == false)
			{
			
				AssetBundleCreateRequest	  abr = AssetBundleMgr.loadOnceAsync("ui/" + resName + ".abd");;
				if (abr == null)
				{
					Debug.LogError(resName + " is null");
				}
				//CpuDebuger.print ("load::"+assetName);
				while (abr.isDone == false)
				{
					yield return null;

				}
			 
		 

				abr.assetBundle.Contains("");
				abc = new AssetBundleCache();
				abc.assetBundle = abr.assetBundle;
				allitems[resName] = abc;
				Debug.Log("load new :"+resName);

			}
 
			abc.used++;
	 
			if (onLoad != null)
			{
				onLoad(abc.assetBundle);
			}

		}

		public static void unload(string resName)
		{
			AssetBundleCache abc = getOneCache(resName);
			if (abc == null) return;
			abc.used--;
			if (abc.used <= 0)
			{
				abc.assetBundle.Unload(true);
				allitems.Remove(resName);
				Debug.Log("unload:"+resName);
			}
		}
	
	}

	class  AssetBundleCache
	{
		public AssetBundle assetBundle;
		public int used=0;

	}
}