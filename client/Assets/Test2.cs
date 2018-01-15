using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.socket;
using UnityEngine;

public class Test2 : MonoBehaviour {
 List<string> usingItems=new List<string>();
	// Use this for initialization
	void Start () {
		CoreLibCallBack.isReourcesLoadMode = true;
		CoreLibCallBack.localResPath = "c:/m1_res";
	}
	
	// Update is called once per frame
	void Update () {
		
	}

	private void OnGUI()
	{
		if (GUILayout.Button("load hero"))
		{
		StartCoroutine(AssetBundleManager.load("npc/hero.abd",usingItems, (ab) =>
			{
				Instantiate(ab.LoadAsset<GameObject>("hero"));
				 
			}));

		}
			if (GUILayout.Button("unload hero"))
                         		{
                 			        AssetBundleManager.unload(usingItems);
                         		}
		if (GUILayout.Button("load and unload hero"))
		{
			StartCoroutine(AssetBundleManager.load("npc/hero.abd",usingItems, (ab) =>
			{
				Instantiate(ab.LoadAsset<GameObject>("hero"));
				AssetBundleManager.unload(usingItems);
			}));
		}
	}
}
