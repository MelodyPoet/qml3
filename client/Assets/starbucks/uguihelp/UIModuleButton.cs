using System.Collections;
using System.Collections.Generic;
using starbucks.uguihelp;
using starbucks.ui;
using UnityEngine;

public class UIModuleButton : MonoBehaviour
{
	public enum ClickMode
	{
		OPEN,CLOSE,SWAP
	}
	public int moduleID;
	public ClickMode clickMode;
	public string tabName;
	// Use this for initialization
	void Start ()
	{
		UIEventListener.Get(gameObject).onClick = (go) =>
		{
			if (clickMode == ClickMode.OPEN)
			{
				new ShowViewCmd(moduleID, tabName).excute();
			}
			else
			{
				new HideViewCmd(moduleID).excute();
			}
		};
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
