using System.Collections;
using System.Collections.Generic;
using modules.scene.main;
using starbucks.basic;
using starbucks.socket;
using starbucks.socket.udp;
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
		AssetBundle assetBundle = AssetBundleManager.loadSimple("gameroot.abd");
 
         GameObject gameroot=	Instantiate(assetBundle.LoadAsset<GameObject>("gameroot"));
		assetBundle.Unload(false);
		TransformUtils.resetMatrix(gameroot.transform);
		UguiRoot.init(gameroot.transform.Find("uiroot").GetComponent<Canvas>());

		ModulesManager.init();
		Debug.Log("init main");
		//EventDispatcher.globalDispatcher.DispatchEvent(ModuleEvent.SHOW_MAIN_VIEW, ModuleEnum.PASSPORT);
		new ShowViewCmd(ModuleEnum.PASSPORT).excute();
		gameObject.AddComponent<GameScene>();
		new RegRspdClassCmd().execute();
		yield break;
	}
	
	// Update is called once per frame
	void Update () {
		UdpService.instance.dispachAllEvent();
	}
}
