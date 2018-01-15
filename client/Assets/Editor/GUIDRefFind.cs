using UnityEngine;
using System.Collections;
using UnityEditor;
using System.Linq;
using System.IO;
using System.Text.RegularExpressions;
using System.Collections.Generic;

/// <summary>
/// 解决项目中 一样的资源（名字或者路径不同）存在两份的问题  （多人做UI出现的问题， 或者美术没有管理好资源）
/// 如果是要替换资源的话， 那就直接替换好了
/// 
/// 以上可以这么操作的基础是，你的Unity项目内的.prefab .Unity 都可以直接用文本开看到数据，而不是乱码（二进制）。这一步很关键，怎么设置呢？
/// 打开项目Unity编辑器：Edit —-> Project Settings —-> Editor 这样就会调到你的Inspector面板的Editor Settings 
/// 设置 Asset Serialization 的Mode类型为：Force Text(默认是Mixed); 这样你就能看到你的prefab文件引用了哪些贴图，字体，prefab 等资源了
/// </summary>
public class GUIDRefFind : EditorWindow
{
	private static GUIDRefFind _window;
 

	private string _oldGuid;
 
	private bool isContainScene = true;
	private bool isContainPrefab = true;
	private bool isContainMat = true;
	private bool isContainAsset = false;

	private List<string> withoutExtensions = new List<string>();
 

	[MenuItem("gametools/RefFind/GUIDRefFindWin")]   // 菜单开启并点击的   处理
	public static void GUIDRefFindWin()
	{

		// true 表示不能停靠的
		_window = (GUIDRefFind)EditorWindow.GetWindow(typeof(GUIDRefFind), true, "引用查找 (●'◡'●)");
		_window.Show();

	}
	DefaultAsset  doFolder;
	void OnGUI()
	{
		// 要被替换的（需要移除的）
		GUILayout.Space(20);

		doFolder = EditorGUILayout.ObjectField("查询目录", doFolder, typeof(DefaultAsset), true) as DefaultAsset;
  

		// 在那些类型中查找（.unity\.prefab\.mat）
		GUILayout.Space(20);
		GUILayout.Label("要在哪些类型中查找：");
		EditorGUILayout.BeginHorizontal();

		isContainScene = GUILayout.Toggle(isContainScene, ".unity");
		isContainPrefab = GUILayout.Toggle(isContainPrefab, ".prefab");
		isContainMat = GUILayout.Toggle(isContainMat, ".mat");
		isContainAsset = GUILayout.Toggle(isContainAsset, ".asset");

		EditorGUILayout.EndHorizontal();


		GUILayout.Space(20);
		if (GUILayout.Button("开始查找!"))
		{
			if (EditorSettings.serializationMode != SerializationMode.ForceText)
			{
				UnityEngine.Debug.LogError("需要设置序列化模式为 SerializationMode.ForceText");
				ShowNotification(new GUIContent("需要设置序列化模式为 SerializationMode.ForceText"));
			}
	 
			else if (!isContainScene && !isContainPrefab && !isContainMat && !isContainAsset)
			{
				UnityEngine.Debug.LogError("要选择一种 查找查找的类型");
				ShowNotification(new GUIContent("要选择一种 查找的类型"));
			}
			else   // 执行替换逻辑
			{
				StartFind();
			}
		}
	}

	private void StartFind()
	{
		var path = AssetDatabase.GetAssetPath(Selection.activeObject);
		Debug.LogError(	AssetDatabase.GetImplicitAssetBundleName (path));

	 
		 
		_oldGuid = AssetDatabase.AssetPathToGUID(path);
 
 
		withoutExtensions = new List<string>();
		if (isContainScene)
		{
			withoutExtensions.Add(".unity");
		}
		if (isContainPrefab)
		{
			withoutExtensions.Add(".prefab");
		}
		if (isContainMat)
		{
			withoutExtensions.Add(".mat");
		}
		if (isContainAsset)
		{
			withoutExtensions.Add(".asset");
		}

		Find();
	}

	//void RefreshMat(string findType)
	//{
	//    var guids = AssetDatabase.FindAssets("t:" + findType);  // Material      // 这一句牛逼呀， 直接按照类型获取   file:///D:/Program%20Files/Unity%205.2.0b4/Unity/Editor/Data/Documentation/en/ScriptReference/AssetDatabase.FindAssets.html
	//    foreach (var guid in guids)
	//    {

	//    }
	//}

	/// <summary>
	/// 查找  并   替换 
	/// </summary>
	private void Find()
	{
		if (withoutExtensions == null || withoutExtensions.Count == 0)
		{
			withoutExtensions = new List<string>() { ".prefab", ".unity", ".mat", ".asset" };
		}
	 


		string[] files = Directory.GetFiles(Application.dataPath.Substring(0,Application.dataPath.Length-6)+AssetDatabase.GetAssetPath(doFolder), "*.*", SearchOption.AllDirectories)
			.Where(s => withoutExtensions.Contains(Path.GetExtension(s).ToLower())).ToArray();
		int startIndex = 0;

		if (files == null || files.Length == 0)
		{
			UnityEngine.Debug.Log("没有找到 筛选的引用");
			return;
		}

		EditorApplication.update = delegate ()
		{
			string file = files[startIndex];

			bool isCancel = EditorUtility.DisplayCancelableProgressBar("匹配资源中", file, (float)startIndex / (float)files.Length);

			var content = File.ReadAllText(file);
			if (Regex.IsMatch(content, _oldGuid))
			{
				UnityEngine.Debug.Log(file, AssetDatabase.LoadAssetAtPath<Object>(GetRelativeAssetsPath(file)));

				//content = content.Replace(_oldGuid, _newGuid);

				//File.WriteAllText(file, content);
			}
			else
			{
				//Debug.Log(file);
			}

			startIndex++;
			if (isCancel || startIndex >= files.Length)
			{
				EditorUtility.ClearProgressBar();
				EditorApplication.update = null;
				startIndex = 0;

				AssetDatabase.Refresh();
				UnityEngine.Debug.Log("结束");
			}

		};
	}

	private string GetRelativeAssetsPath(string path)
	{
		return "Assets" + Path.GetFullPath(path).Replace(Path.GetFullPath(Application.dataPath), "").Replace('\\', '/');
	}

}
