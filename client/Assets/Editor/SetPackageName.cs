using UnityEngine;
using System.Collections;
using UnityEditor;

public class SetPackageName : MonoBehaviour {
    [MenuItem ("设置游戏包名/无SDK(每周外网)")]
    static void setNormalPlatform() {
        PlayerSettings.applicationIdentifier = "com.cyyl.wzj";
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android, "inGame;resetShader");
    }

    [MenuItem ("设置游戏包名/小米平台")]
    static void setXiaomiPlatform() {
        PlayerSettings.applicationIdentifier = "com.cyyl.wzj.mi";
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android, "inGame;resetShader;SDK_XIAOMI");
    }

    [MenuItem ("设置游戏包名/九游平台")]
    static void setJiuyouPlatform() {
        PlayerSettings.applicationIdentifier = "com.cyyl.wzj.aligames";
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android, "inGame;resetShader;SDK_JIUYOU");
    }
}
