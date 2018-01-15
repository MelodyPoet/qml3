using System.Reflection;
using UnityEngine;

namespace starbucks.utils
{
	public class AutoFeildSetter : MonoBehaviour
	{
		public Component[] items;
		// Use this for initialization
		void Awake () {
			MonoBehaviour cantainer  =GetComponent<MonoBehaviour>();

			foreach (Component item in items)
			{
				FieldInfo fi = cantainer.GetType().GetField(item.name);
	      
				Debug.LogError("-----------Utils---------------setUiItemWithSameName-----------item----------------" + item.name);
				if (fi.FieldType == typeof(GameObject))
				{
					fi.SetValue(cantainer, item.gameObject);
				}
				else if (fi.FieldType == typeof(Transform))
				{
					fi.SetValue(cantainer, item);

				}
				else
				{
					fi.SetValue(cantainer, item);
				}
			}

		}
		[ContextMenu("复制成代码声明")]
		private   void CopyToClip()
		{
       
			string msg = "";
			foreach (Component item in items)
			{
				msg += "public " + item.GetType().Name + " " + item.name + ";\n";
			}
			GUIUtility.systemCopyBuffer=msg;

		}

	}
}
