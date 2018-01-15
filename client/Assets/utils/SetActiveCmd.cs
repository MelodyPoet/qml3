using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

public class SetActiveCmd : MonoBehaviour,ICommand {
	public string tag;
	public GameObject item;
	public bool isTrue;

 
	#region ICommand implementation
	public void excute ()
	{
		item.SetActive (isTrue);
	}
	#endregion
}
