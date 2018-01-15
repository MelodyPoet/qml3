using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.utils;
using UnityEngine;
using UnityEngine.UI;

public class HurtCmd :ICommand {
	public bool isHero;
	public int point;
	#region ICommand implementation

	public void excute ()
	{
		MovieClip2 roleMc=Test.instance.currentMonster;
		if (isHero)
			roleMc = Test.instance.hero;
		roleMc.Play ("hurt");

		GameObject goHurt = null;
		if (isHero) {
			goHurt = GameObject.Instantiate<GameObject> (Resources.Load<GameObject> ("hurt_text_left"));
			goHurt.transform.SetParent (GameObject.Find ("Canvas").transform);
			goHurt.transform.localPosition =  new Vector3(0,0,0);
			//goHurt.transform.position = roleMc.transform.position;

		} else {
			goHurt = GameObject.Instantiate<GameObject> (Resources.Load<GameObject> ("hurt_text_right"));
			goHurt.transform.SetParent (GameObject.Find ("Canvas").transform);
			goHurt.transform.localPosition =  new Vector3(400,0,0);
		}
		//roleMc.roleHp -= point;
		Test.instance.onUpdateHp();
		goHurt.GetComponentInChildren<Text> ().text = "-" + point;

	}

	#endregion


 
}
