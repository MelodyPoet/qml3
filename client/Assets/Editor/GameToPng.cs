using UnityEditor;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor.Animations;

public class GameToPng  {
 
 
	[MenuItem ("gametools/saveGameToPng/1x")]
	public static void saveGameToPng1()
	{
		save (0);

	}
	[MenuItem ("gametools/saveGameToPng/2x")]
	public static void saveGameToPng2()
	{
		save (2);

	}
	[MenuItem ("gametools/saveGameToPng/3x")]
	public static void saveGameToPng3()
	{
		save (3);

	}
	[MenuItem ("gametools/saveGameToPng/4x")]
	public static void saveGameToPng4()
	{
		save (4);

	}
	[MenuItem ("gametools/saveGameToPng/5x")]
	public static void saveGameToPng5()
	{
		save (5);

	}
	[MenuItem ("gametools/saveGameToPng/6x")]
	public static void saveGameToPng6()
	{
		save (6);

	}
	[MenuItem ("gametools/saveGameToPng/7x")]
	public static void saveGameToPng7()
	{
		save (7);

	}
	[MenuItem ("gametools/saveGameToPng/8x")]
	public static void saveGameToPng8()
	{
		save (8);

	}
	static void save(int scale){
		string desurl;
		if (UnityEngine.Application.platform == UnityEngine.RuntimePlatform.OSXEditor) {
			desurl = "/Users/jackie/Desktop/scene.png";
		} else {
			desurl = "E:/scene.png";
		}
		ScreenCapture.CaptureScreenshot(desurl, scale);
	}
}
