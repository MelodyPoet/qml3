using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.utils;
using UnityEngine;

public class AttackCmd :ICommand {
	public bool isHero;
	#region ICommand implementation

	public void excute ()
	{
		MovieClip2 roleMc=Test.instance.currentMonster;
		if (isHero)
			roleMc = Test.instance.hero;
		roleMc.Play ("attack");

	}

	#endregion


 
}
