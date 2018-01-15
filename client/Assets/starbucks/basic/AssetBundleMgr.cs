using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using starbucks.socket;
using Object = UnityEngine.Object;

namespace starbucks.basic
{



public enum ResPath
    {
        streamAsset,
        persistentDataPath,
        web,
        autoStreamOrPersistent
    }
public enum AssetBundleGroup
{
    DONOT_UNLOAD,
    SCENE_CHANGE_UNLOAD
}

public class AssetBundleMgr
{
    public static string res_url;
     
    public static HashSet<string> inStreamFolder = new HashSet<string>();

    public static LoadingAssetBundle UIRES, UIMDRES, UIRES_SINGLE;


 


	[Obsolete ("旧版本ngui 才使用的 资源管理模式 ")]
    public static GameObject getUIAsset(string name)
    {
 
            return UIMDRES.ab.LoadAsset<GameObject>(name);
       
      
    }
    [Obsolete ("旧版本ngui 才使用的 资源管理模式 ")]
    public static T getUIResAsset<T>(string name) where T:Object
    {
            return UIRES.ab.LoadAsset<T>(name);
    }
    static Dictionary<AssetBundleGroup, HashSet<LoadingAssetBundle>> GroupItems =
        new Dictionary<AssetBundleGroup, HashSet<LoadingAssetBundle>>();

    static Dictionary<string, LoadingAssetBundle> all = new Dictionary<string, LoadingAssetBundle>();


    public static AssetBundle getAbd(string path)
    {
        return all[path].ab;
    }

    public static void unloadGroup(AssetBundleGroup group)
    {
        if (GroupItems.ContainsKey(group) == false)
            return;
        HashSet<LoadingAssetBundle> items = GroupItems[group];
        foreach (var item in items)
        {
            all.Remove(item.path);
            item.unload();
        }
        items.Clear();
        Resources.UnloadUnusedAssets();
        Debug.Log("unloadGroup");
    }

//		public static void unloadAbd(string path){
//				all [path].unload (false);
//				all.Remove (path);
//		}
    public static IEnumerator loadAbdGameObjectInstance(string path, LoadingAssetBundle outlab, bool forOnce = false,

       bool asyncModel=false,AssetBundleGroup group = AssetBundleGroup.SCENE_CHANGE_UNLOAD,bool createNew=true,String itemName=null)
    {
        yield return loadAbd(path, outlab, forOnce, group);
      
            if (asyncModel)
            {
                AssetBundleRequest rqst =
                    outlab.ab.LoadAssetAsync(
                        string.IsNullOrEmpty(itemName) ? outlab.ab.GetAllAssetNames()[0] : itemName);

                yield return rqst;
                outlab.tempGo = rqst.asset as GameObject;

            }
            else
            {

                outlab.tempGo =
                    outlab.ab.LoadAsset<GameObject>(string.IsNullOrEmpty(itemName)
                        ? outlab.ab.GetAllAssetNames()[0]
                        : itemName);
            }
        
        if(createNew&&outlab.tempGo!=null){
        outlab.tempGo = GameObject.Instantiate<GameObject>(outlab.tempGo);
        }
    }

    public static IEnumerator loadAbd(string path, LoadingAssetBundle outlab, bool forOnce = false,
        
        AssetBundleGroup group = AssetBundleGroup.SCENE_CHANGE_UNLOAD,string itemName=null)
    {
        
        LoadingAssetBundle lab = null;

        
        if (forOnce == false)
        {
            if (all.ContainsKey(path))
            {
                lab = all[path];
        
                while (lab.ab == null)
                {
                    yield return 0;
                }
                outlab.ab = lab.ab;
                yield break;
            }
            all[path] = lab = new LoadingAssetBundle();
            lab.path = path;
            if (GroupItems.ContainsKey(group) == false)
                GroupItems[group] = new HashSet<LoadingAssetBundle>();
            GroupItems[group].Add(lab);
        }
        else
        {
            lab = new LoadingAssetBundle();
            
        }
 

//			outlab.ab =	lab.ab=AssetBundle.LoadFromFile(ResFileInit.fullPath(path,ResPath.autoStreamOrPersistent,false));
//			yield break;
        AssetBundleCreateRequest rqst =
            AssetBundle.LoadFromFileAsync(fullPath(path, ResPath.autoStreamOrPersistent, false));

        yield return rqst;
        if (rqst.assetBundle == null)
        {
            Debug.LogError("loaderror:::::::" + path);
        }
        outlab.ab = lab.ab = rqst.assetBundle;

//				WWW www = LoadStream(path);
//				Debug.Log ("loaded:::::::"+path);
//				while (!www.isDone) {
//						yield return new WaitForEndOfFrame();
//				}
//			
//				if (www.error != null) {
//						 Debug.LogError ("loaderror:::::::"+www.error+"\n"+path);
//						yield break;
//				}
//						outlab.ab =	lab.ab = www.assetBundle;

        //outlab.www=lab.www = www;
    }


//		public static string getFinalPath(string url){
//				string  resPath = Application.persistentDataPath;
//				if (url.Contains("uiinit")) {
//						resPath = Application.streamingAssetsPath;
//                      
//				} else {
//						if (Application.isEditor) {
//								//resPath = "file://" + resPath;
//						} else {
//								resPath = "file:///" + resPath;
//
//						}
//				}
//				return resPath + "/" + url;
//		}
    public static AssetBundle loadOnce(string path)
    {
        return AssetBundle.LoadFromFile(fullPath(path, ResPath.autoStreamOrPersistent, false));
    }

    public static void loadAndDestroy(string path, Action<AssetBundle> callback)
    {
 
            AssetBundle ab = loadOnce(path);
            callback.Invoke(ab);
            ab.Unload(false);
       
    }

    public static AssetBundle loadForShared(string path)
    {
        if (all.ContainsKey(path))
        {
            return all[path].ab;
        }
        LoadingAssetBundle abm = all[path] = new LoadingAssetBundle();
        abm.path = path;

        abm.ab = AssetBundle.LoadFromFile(fullPath(path, ResPath.autoStreamOrPersistent, false));
        return abm.ab;
    }

    public static AssetBundleCreateRequest loadOnceAsync(string path)
    {
        return AssetBundle.LoadFromFileAsync(fullPath(path, ResPath.autoStreamOrPersistent, false));
    }

    public static WWW LoadStream(string url)
    {
        WWW www;
        Debug.Log(Application.persistentDataPath);

        www = new WWW(fullPath(url, ResPath.autoStreamOrPersistent));


        return www;
    }

    public static string fullPath(string fileName, ResPath pathMode, bool forWWW = true)
    {
        switch (pathMode)
        {
            case ResPath.web:
                return res_url + "/" + fileName;
            case ResPath.persistentDataPath:
                if (forWWW)
                {
                    
                     return  "file://" + CoreLibCallBack.persistentDataPath + "/" + fileName;
                }
                return CoreLibCallBack.persistentDataPath + "/" + fileName;
            case ResPath.streamAsset:
                if (forWWW && Application.isEditor)
                {
                    return "file://" + Application.streamingAssetsPath + "/m1res/" + fileName;
                }
                return Application.streamingAssetsPath + "/m1res/" + fileName;
            case ResPath.autoStreamOrPersistent:

                return fullPath(fileName,
                    inStreamFolder.Contains(fileName) ? ResPath.streamAsset : ResPath.persistentDataPath, forWWW);
        }
        return null;
    }


}

public class LoadingAssetBundle
{
    public void unload()
    {
        if (ab != null)
            ab.Unload(false);

        path = null;
        ab = null;

        tempGo = null;
    }

    public AssetBundle ab = null;
    public string path = null;

    public GameObject tempGo;
    //public WWW www;
}
}