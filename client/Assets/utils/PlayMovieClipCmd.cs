using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.utils;
using UnityEngine;

public class PlayMovieClipCmd : MonoBehaviour,ICommand {
	public string tag;
	public MovieClip2 mc;
	public string playLab;

 
	#region ICommand implementation
	public void excute ()
	{
		mc.Play (playLab);
	}
	#endregion
}
