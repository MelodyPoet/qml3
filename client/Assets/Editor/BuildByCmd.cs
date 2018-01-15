using UnityEditor;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor.Animations;

public class BuildByCmd  {
 
 
	public static void MakeAssets()
	{
 
		string desurl = System.Environment.GetEnvironmentVariable ("asset_temp_out") ;
		BuildPipeline.BuildAssetBundles (desurl, BuildAssetBundleOptions.None, EditorUserBuildSettings.activeBuildTarget);
 
 	}
	[MenuItem ("gametools/build exe")]
	public static void MakePlayer()
	{

		string desurl = System.Environment.GetEnvironmentVariable ("asset_temp_out") ;
        BuildPipeline.BuildPlayer(new string[]{"Assets/all/main.unity"},desurl, BuildTarget.StandaloneWindows, BuildOptions.Development);

	}
}
