using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class ParamsSet : MonoBehaviour
{

    public string[] keys;
    public Dictionary<string,string> sets;

    void Awake()
    {
        sets = new Dictionary<string, string>();
        foreach (string item in keys)
        {
						 
            int pos =	item.IndexOf("=");
            if (pos == -1)
            {
							
                continue;
            }
            sets.Add(item.Substring(0, pos), item.Substring(pos + 1));
        }
						 
						
		 
    }

    public string getValue(string key)
    {
        if (hasValue(key) == false)
            return null;
        return sets[key];
    }

    public bool hasValue(string key)
    {
        return sets.ContainsKey(key);
    }
	
	 
}
