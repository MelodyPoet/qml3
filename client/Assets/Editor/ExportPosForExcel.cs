using System.Collections;
using System.Reflection;
using UnityEditor;
using UnityEngine;

public class ExportPosForExcel : EditorWindow {

	public static  void getExcelPos(Transform item,ref string info)
	{
		info += (int)(item.transform.position.x * 10) + "_";
		info += (int)(item.transform.position.z * 10) + "_";
		info += (int)item.transform.rotation.eulerAngles.y + "|";
	}

	[MenuItem("gametools/getExcelPos")]
	static void getExcelPos () {
		string str = "";
		foreach (GameObject item in Selection.gameObjects)
		{
			getExcelPos(item.transform, ref str);

		}
		if (str != "") {
			str = str.TrimEnd("|".ToCharArray());
						GUIUtility.systemCopyBuffer = str;
			Debug.LogError(str);
		}
	}

    [MenuItem("gametools/getExcelRect")]
    static void getExcelRect()
    {
        string str = "";
        str= (int)Mathf.Min(Selection.gameObjects[0].transform.position.x, Selection.gameObjects[1].transform.position.x)+"|";
        str +=(int) Mathf.Min(Selection.gameObjects[0].transform.position.z, Selection.gameObjects[1].transform.position.z) + "|";
        str +=(int) Mathf.Abs(Selection.gameObjects[0].transform.position.x- Selection.gameObjects[1].transform.position.x) + "|";
        str +=(int) Mathf.Abs(Selection.gameObjects[0].transform.position.z - Selection.gameObjects[1].transform.position.z) ;
      
        if (str != "")
        {
         
            GUIUtility.systemCopyBuffer = str;
            Debug.LogError(str);
        }
    }
}