using System;
using UnityEditor;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Threading;
using UnityEngine;
using UnityEditor.Animations;
using UnityEngine.UI;
 

public class MakeTempplateModule
{

    static string srcPath = "Assets/starbucks/ui/module_template/src";
    [MenuItem("Assets/模块代码/创建完整新模块")]
 
    public static void CreateNewModule()
    {
        DefaultAsset selItem = Selection.activeObject as DefaultAsset;
        
        if (selItem == null)
        {
            EditorUtility.DisplayDialog("error", "需要在某个目录下", "OK");
            return;
        }
        string targetPath = AssetDatabase.GetAssetPath(selItem);
       if ( EditorUtility.DisplayDialog("error", "确定要在"+AssetDatabase.GetAssetPath(selItem)+"/下 创建吗？" , "确定","取消")==false)return;
        Debug.Log(AssetDatabase.GetAssetPath(selItem));
        FileUtil.CopyFileOrDirectory(srcPath+ "/TemplateEvent.txt", targetPath+ "/TemplateEvent.txt");
        FileUtil.CopyFileOrDirectory(srcPath + "/TemplateModule.txt", targetPath + "/TemplateModule.txt");
        FileUtil.CopyFileOrDirectory(srcPath + "/main", targetPath + "/main");
        FileUtil.CopyFileOrDirectory(srcPath + "/model", targetPath + "/model");

        foreach (var item in Directory.GetFiles(targetPath, "*.txt",SearchOption.AllDirectories))
        {
            Debug.Log(item);
            ReplaceTemplate(item, selItem.name, "main");
        }
       ;

        Thread.Sleep(500);
        AssetDatabase.Refresh();
    }
    [MenuItem("Assets/模块代码/创建模块新部件")]

    public static void CreateNewModulePart()
    {
        DefaultAsset selItem = Selection.activeObject as DefaultAsset;

        if (selItem == null)
        {
            EditorUtility.DisplayDialog("error", "需要在某个目录下", "OK");
            return;
        }
        string targetPath = AssetDatabase.GetAssetPath(selItem);
        if (EditorUtility.DisplayDialog("error", "确定要在" + AssetDatabase.GetAssetPath(selItem) + "/下 创建吗？", "确定", "取消") == false) return;
        Debug.Log(AssetDatabase.GetAssetPath(selItem));
 
        FileUtil.ReplaceDirectory(srcPath + "/other", targetPath );

        string nsName = Directory.GetParent(targetPath).Name;
      Debug.Log(nsName);
        foreach (var item in Directory.GetFiles(targetPath, "*.txt", SearchOption.AllDirectories))
        {
            Debug.Log(item);
            ReplaceTemplate(item, nsName, selItem.name);
        }
        ;

        Thread.Sleep(500);
        AssetDatabase.Refresh();
    }




    public static void ReplaceTemplate(string filePath,string nsName,string subName)
    {
        string moduleName = nsName.Substring(0, 1).ToUpper() + nsName.Substring(1);
        string partName = moduleName;
        if (subName.Equals("main") == false)
        {
            partName= subName.Substring(0, 1).ToUpper() + subName.Substring(1);
        }
        string content = File.ReadAllText(filePath);
        Debug.Log(content);
        content = content.Replace("{-1}", nsName);
        content =   content.Replace("{0}", moduleName);
        content = content.Replace("{1}", subName);

        content = content.Replace("{2}", partName);
        File.WriteAllText(filePath,content);
        File.Move(filePath, filePath.Replace(".txt",".cs").Replace("Template", partName));

    }






}
