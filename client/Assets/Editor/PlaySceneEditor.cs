using UnityEngine;
using System.Collections;
using UnityEditor;
using UnityEditor.SceneManagement;

 
	public class PlaySceneEditor : EditorWindow
	{
	 
 

	[MenuItem("gametools/自动运行/游戏")]
		static void RunMain()
		{
		EditorSceneManager.playModeStartScene = AssetDatabase.LoadAssetAtPath<SceneAsset> ("Assets/A1/main.unity");
		}
	[MenuItem("gametools/自动运行/游戏",true)]
	static bool IsRunMain()
	{
		return EditorSceneManager.playModeStartScene == null;
	}

	[MenuItem("gametools/自动运行/当前场景")]
	static void RunSelf()
	{
		EditorSceneManager.playModeStartScene = null;
	}
	[MenuItem("gametools/自动运行/当前场景",true)]
	static bool IsRunSelf()
	{
		return	EditorSceneManager.playModeStartScene != null;
	}
	}