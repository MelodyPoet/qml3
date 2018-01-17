using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
 using System.IO;
using System.Reflection;
using System.Linq;
using System.Xml;
using starbucks.basic;
using UnityEngine.Profiling;

public class BaseData
{
    public static bool isInit;
   
 
	private static   ByteArray allDataBytes;
	private static Dictionary<string,int> FilePosMap = new Dictionary<string, int>();
	 
    public static Dictionary<int, NpcBaseVo> NpcBaseMap;
    public static Dictionary<int, MapBaseVo> MapBaseMap;
 
    public static Dictionary<int, IList<NpcLayoutBaseVo>> NpcLayoutBaseMap;
  
    public static XmlNodeList ServerListXml;

    public static IEnumerator init()
    {
 
 		WWW www = null;
		 
 
			www = new WWW (PathManager.fullPath( "data.tbl", ResPath.autoStreamOrPersistent,true));
			yield return www;
			if (!string.IsNullOrEmpty (www.error)) {
				Debug.Log (www.error + "::" + www.url);
			}

			allDataBytes = new ByteArray (www.bytes);
		 
        while (allDataBytes.stream.Length - allDataBytes.stream.Position > 64)
        {
            int pos = (int)allDataBytes.stream.Position;
            int fileSize = allDataBytes.readInt();

						 
            string fileName = allDataBytes.readString();
            allDataBytes.stream.Position =	pos + 64;
            FilePosMap[fileName] = pos + 60;
            allDataBytes.stream.Position += fileSize;
			 
        }
			
			
				 

        //Debug.Log("<<<BaseData>>>0>");
        
        //Debug.Log("<<<BaseData>>>0.5>");
        yield return GlobalCoroutine.instance.StartCoroutine(readMap<int, NpcBaseVo>());
        //Debug.Log("<<<BaseData>>>1>");
        yield return GlobalCoroutine.instance.StartCoroutine(readMap<int, MapBaseVo>());
        //Debug.Log("<<<BaseData>>>2>");
				 
        yield return GlobalCoroutine.instance.StartCoroutine(readMapList<int, NpcLayoutBaseVo>());
        //Debug.Log("<<<BaseData>>>25>");
       
     
         if (isInit)
            yield break;
        isInit = true;

      

       

       // View.gameLoading.showInitNext();
        yield return null;
    }

 

    private static IEnumerator readMap<Tkey, Tvalue>()
    {
        //Debug.Log(typeof(Tvalue));
        string voName = typeof(Tvalue).Name;
        yield return new WaitForEndOfFrame();
        allDataBytes.stream.Position = FilePosMap[voName]; 
        typeof(BaseData).GetField(voName.Replace("Vo", "Map")).SetValue(null, allDataBytes.readMap<Tkey, Tvalue>());

    }

    private static IEnumerator readMapList<Tkey, Tvalue>(string groupKey = "ID", string feildName = null) where Tvalue : new()
    {
        Debug.Log(typeof(Tvalue));
        string voName = typeof(Tvalue).Name;
        yield return new WaitForEndOfFrame();
        allDataBytes.stream.Position = FilePosMap[voName]; 
        List<Tvalue> arr = allDataBytes.readArray<Tvalue>();
        Dictionary<Tkey, IList<Tvalue>> GroupMap = new Dictionary<Tkey, IList<Tvalue>>();
        FieldInfo f = typeof(Tvalue).GetField(groupKey);
        foreach (Tvalue item in arr)
        {
            Tkey key = (Tkey)f.GetValue(item);
            if (GroupMap.ContainsKey(key) == false)
                GroupMap[key] = new List<Tvalue>();
            GroupMap[key].Add(item);
        }
        if (feildName == null)
        {
            typeof(BaseData).GetField(voName.Replace("Vo", "Map")).SetValue(null, GroupMap);
        }
        else
        {
            typeof(BaseData).GetField(feildName).SetValue(null, GroupMap);

        }
    }


    private static void fillMap<T>(Dictionary<int, T> map, int start, int end)
    {

        T vo = default(T);
        for (int i = start; i <= end; i++)
        {
            if (map.ContainsKey(i))
            {
                vo = map[i];
            }
            else
            {
                map[i] = vo;
            }
        }
    }
}