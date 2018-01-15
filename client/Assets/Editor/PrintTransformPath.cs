using System.Collections;
using System.Reflection;
using UnityEditor;
using UnityEngine;

public class PrintTransformPath : EditorWindow {


	[MenuItem("gametools/PrintTransformPath")]
	static void _PrintTransformPath () {
		string str = "";
		Transform t = Selection.activeTransform;
	while (t!=null) {
			str=t.name+"/"+str;
			t=t.parent;
			 
				}
	str=	str.TrimEnd ('/');
				GUIUtility.systemCopyBuffer = str;
	 
			Debug.LogError(str);
		 
	}
}