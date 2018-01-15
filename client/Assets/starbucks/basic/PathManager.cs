using System.Collections.Generic;
using starbucks.socket;
using UnityEngine;

namespace starbucks.basic
{
    public enum ResPath
    {
        streamAsset,
        persistentDataPath,
        web,
        autoStreamOrPersistent
    }
    public class PathManager
    {
        public static string res_url;
     
        public static HashSet<string> inStreamFolder = new HashSet<string>();
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
}