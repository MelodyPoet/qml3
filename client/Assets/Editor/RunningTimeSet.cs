using UnityEditor;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor.Animations;

public class RunningTimeSet  {
 
 
	private static Object tempObj;
	[MenuItem ("gametools/copy material")]
	public static void copyMaterial()
	{

		tempObj=Selection.activeGameObject.GetComponent<Renderer> ().material;
	}
	[MenuItem ("gametools/paste material")]
	public static void pasteMaterial()
	{

		Selection.activeGameObject.GetComponent<Renderer> ().material=tempObj as Material;
	}
}
