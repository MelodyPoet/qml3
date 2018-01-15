using System.Collections;
using System.Reflection;
using UnityEditor;
using UnityEngine;

public class ReplaceGameObjectInScene : EditorWindow
{
	private static ReplaceGameObjectInScene _window;
	private GameObject[] _sourceOld;
	private GameObject  _sourceNew;
	private bool ignoreScale = false;
	private bool ignoreRotate= false;
	private bool ignoreRotateXZ= false;
	[MenuItem("gametools/ReplaceGameObjectInScene")]
	public static void GUIDRefReplaceWin()
	{

		// true 表示不能停靠的
		_window = (ReplaceGameObjectInScene)EditorWindow.GetWindow(typeof(ReplaceGameObjectInScene), true, "gameObject实例替换 (●'◡'●)");
		_window.Show();

	}

	void OnGUI()
	{
		// 要被替换的（需要移除的）
		GUILayout.Space(20);
		if (_sourceOld != null)
		{
			foreach (var item in _sourceOld)
			{
				EditorGUILayout.ObjectField("旧的资源", item, typeof(GameObject), true);
			}
		}


		
		_sourceNew = EditorGUILayout.ObjectField("新的资源", _sourceNew, typeof(GameObject), true) as GameObject;
		if (GUILayout.Button("添加选中为被替换对象"))
         		{
         			_sourceOld = Selection.gameObjects;
         		}
		ignoreScale = EditorGUILayout.Toggle("忽略缩放", ignoreScale);
		ignoreRotate = EditorGUILayout.Toggle("忽略Y旋转", ignoreRotate);
		ignoreRotateXZ = EditorGUILayout.Toggle("忽略XZ旋转", ignoreRotateXZ);
		if (GUILayout.Button("替换全部旧对象"))
		{
			_ReplaceGameObjectInScene();
		}
	}

	  void _ReplaceGameObjectInScene ()
	{
		 
		 
	GameObject newItem=	_sourceNew;
	 
		Debug.Log(newItem);
		for (int i = 0; i < _sourceOld.Length; i++)
		{
			 
			//items[1].transform.position
			GameObject go = GameObject.Instantiate(newItem);
			go.transform.parent = _sourceOld[i].transform.parent;
			go.transform.localPosition = _sourceOld[i].transform.localPosition;
		Vector3 rot=	_sourceOld[i].transform.localRotation.eulerAngles;
			if (ignoreRotate) rot.y = newItem.transform.localRotation.eulerAngles.y;
			if (ignoreRotateXZ)
			{
				rot.x = newItem.transform.localRotation.eulerAngles.x;
				rot.z = newItem.transform.localRotation.eulerAngles.z;
			}
			go.transform.localRotation = Quaternion.Euler(rot);
			if(ignoreScale==false)
			go.transform.localScale = _sourceOld[i].transform.localScale;
			GameObject.DestroyImmediate(_sourceOld[i]);
		}
		

	}
}