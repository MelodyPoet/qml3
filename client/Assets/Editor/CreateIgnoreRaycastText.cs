using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using UnityEngine.UI;
public class CreateIgnoreRaycastText{

    [MenuItem("GameObject/UI/Text")]
    static void CreatImage()
    {
        if (Selection.activeTransform)
        {
            if (Selection.activeTransform.GetComponentInParent<Canvas>())
            {
                GameObject go = new GameObject("Text", typeof(Text));
                go.GetComponent<Text>().raycastTarget = false;
                go.GetComponent<Text>().text = "New Text";
                go.transform.SetParent(Selection.activeTransform);
                go.transform.localPosition = Vector3.zero;
                go.transform.localScale = Vector3.one;
            }
        }
    }
}
