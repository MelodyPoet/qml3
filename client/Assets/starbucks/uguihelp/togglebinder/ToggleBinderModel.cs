using System.Collections.Generic;
using UnityEngine;

namespace starbucks.uguihelp.togglebinder
{
	public class ToggleBinderModel : MonoBehaviour
	{

 
		public Dictionary<string, object> dicValues=new Dictionary<string, object>();
		public Dictionary<string, HashSet<object>> dicSets=new Dictionary<string, HashSet<object>>();
		public Dictionary<string, List<int>> dicIntList=new Dictionary<string, List<int>>();

		public void setValue(string key,object value)
		{
			dicValues[key] = value;
		}
		public void addSetValue(string key,object value)
		{
			if(dicSets.ContainsKey(key)==false)dicSets.Add(key,new HashSet<object>());
			dicSets[key].Add(value);
		}
		public void removeSetValue(string key,object value)
		{
			if (dicSets.ContainsKey(key) == false) return;
			dicSets[key].Remove(value);
		}
	
		public T getValue<T>(string key)
		{
			return (T)dicValues[key];
		}
		public HashSet<object> getSet (string key)
		{
			return dicSets[key];
		}

		private void Update()
		{
			if (Input.GetKeyDown(KeyCode.P))
			{
				
				Debug.Log(getValue<int>("score"));
				Debug.Log(getValue<int>("banker"));
				Debug.Log(getValue<int>("round"));
			}

		}

		public List<int> getListValue(string key)
		{
			return dicIntList[key];
		}

		public void setListValue(string key, int listIndex, int value)
		{
			if(dicIntList.ContainsKey(key)==false) dicIntList[key]=new List<int>();

			List<int> list = dicIntList[key];
			if (list.Count <= listIndex)
			{
				for (int i = 0; i < listIndex - list.Count + 1; i++)
				{
					list.Add(0);
				}
			}

			list[listIndex] = value;

		}
	}
}
