using UnityEditor;
using System.Collections;
using System.Collections.Generic;
using starbucks.utils;
using UnityEngine;
using UnityEditor.SceneManagement;

public class AssetBundlesMaker : EditorWindow {
  


				[MenuItem ("gametools/abd/设置导出名称/导出到npc目录")]
				static void exportNpc () {
						checkSelection ();
						setAssetBundleName (Selection.objects, "npc");
				}


				[MenuItem ("gametools/abd/设置导出名称/导出到weapon目录")]
				static void exportWeapon () {
						checkSelection ();

						setAssetBundleName (Selection.objects, "weapon");

				}

				[MenuItem ("gametools/abd/设置导出名称/导出到skin目录")]
				static void exportSkin () {
						checkSelection ();

						setAssetBundleName (Selection.objects, "skin");
				}

				[MenuItem ("gametools/abd/设置导出名称/导出到map目录")]
				static void exportMap () {
						checkSelection ();

						setAssetBundleName (Selection.objects, "map");
				}

				[MenuItem ("gametools/abd/设置导出名称/导出到根目录")]
				static void exportRoot () {
						checkSelection ();

						setAssetBundleName (Selection.objects, "");
				}

				static void checkSelection(){
						if (Selection.objects.Length == 0) {
								Debug.Log ("请先选择需要设置assetBundleName的对象！");
								return;
						}
				}

				static void setAssetBundleName(Object[] objs, string firstPath){
						foreach (Object obj in objs) {
								if (obj.GetType () == typeof(MovieClipAsset) ||obj.GetType () == typeof(UnityEditor.SceneAsset) ||   obj.GetType () == typeof(GameObject) || obj.GetType () == typeof(Texture2D) || obj.GetType () == typeof(Material)) {
										string objPath = AssetDatabase.GetAssetPath (obj);
										var importer = AssetImporter.GetAtPath (objPath);
										if(firstPath.Length == 0)
												importer.assetBundleName = obj.name + ".abd";
										else
												importer.assetBundleName = firstPath + "/" + obj.name + ".abd";

								} else {
										Debug.Log ("所选的Object中包含非材质、预设、贴图类型, 它就是: " + obj.name +":"+ obj.GetType ()+ " ，不过没关系，我们不对其设置AssetBundleName");
								}
						}
						EditorApplication.RepaintHierarchyWindow ();

						Debug.Log ("设置成功！");
				}
		 

		[MenuItem ("gametools/abd/全部导出")]
	static void exportSelect () {
				
				//AssetDatabase.RemoveAssetBundleName ("selfname", true);
				string desurl;
				if (UnityEngine.Application.platform == UnityEngine.RuntimePlatform.OSXEditor) {
						desurl = "/Users/jackie/Desktop/m1_res";
				} else {
						desurl = "C:/m1_res";
				}

			desurl = Application.streamingAssetsPath;
				BuildPipeline.BuildAssetBundles (desurl,BuildAssetBundleOptions.None,EditorUserBuildSettings.activeBuildTarget);
	 

	}

}
