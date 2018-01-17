using System.Collections;
using System.Collections.Generic;
using modules.scene;
using starbucks.basic;
using starbucks.uguihelp;
using starbucks.utils;
using UnityEngine;
using UnityEngine.UI;

public class DieCmd :ICommand {
	public bool isHero;
	public int point;
	#region ICommand implementation

	public void excute ()
	{
		MovieClip2 roleMc=Test.instance.currentMonster;
		if (isHero)
			roleMc = Test.instance.hero;
		roleMc.Play ("die");

		GameObject goHurt = null;
		if (isHero) {
			goHurt = GameObject.Instantiate<GameObject> (Resources.Load<GameObject> ("hurt_text_left"));
			goHurt.transform.SetParent (UguiRoot.rootCanvas.transform);
			goHurt.transform.localPosition =  new Vector3(0,0,0);
			//goHurt.transform.position = roleMc.transform.position;

		} else {
			goHurt = GameObject.Instantiate<GameObject> (Resources.Load<GameObject> ("hurt_text_right"));
			goHurt.transform.SetParent (UguiRoot.rootCanvas.transform);
			goHurt.transform.localPosition =  new Vector3(400,0,0);
		}
		//roleMc.roleHp=0;
		//Test.instance.onUpdateHp();
		goHurt.GetComponentInChildren<Text> ().text = "-" + point;
		GameObject.Destroy(Test.instance.currentMonster.transform.parent.gameObject,0.5f);
		GameObject.Destroy(Test.instance);
		EventDispatcher.globalDispatcher.DispatchEvent(SceneEvent.EVENT_SCENE_DIE, isHero?0:1);


	}

	#endregion


 
}
