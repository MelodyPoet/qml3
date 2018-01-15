﻿using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.socket;
using starbucks.uguihelp;
using starbucks.ui;
using starbucks.utils;
using UnityEngine;

public class Main : MonoBehaviour {

	// Use this for initialization
	IEnumerator Start ()
	{
		gameObject.AddComponent<GlobalCoroutine>();
		CoreLibCallBack.isReourcesLoadMode = true;
		CoreLibCallBack.localResPath = "c:/m1_res";
		AssetBundle assetBundle = AssetBundleMgr.loadOnce("gameroot.abd");
 
         GameObject gameroot=	Instantiate(assetBundle.LoadAsset<GameObject>("gameroot"));
		TransformUtils.resetMatrix(gameroot.transform);
		UguiRoot.init(gameroot.transform.Find("uiroot").GetComponent<Canvas>());

		ModulesManager.init();
		Debug.Log("init main");
		EventDispatcher.globalDispatcher.DispatchEvent(ModuleEvent.SHOW_MAIN_VIEW, ModuleEnum.PASSPORT);
		yield break;
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}