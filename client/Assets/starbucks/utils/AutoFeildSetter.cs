using System.Reflection;
using UnityEngine;

namespace starbucks.utils
{
	public class AutoFeildSetter : MonoBehaviour
	{
		public Object[] items;
		// Use this for initialization
	public	void apply (MonoBehaviour cantainer) {
			 

			foreach (Object item in items)
			{
				FieldInfo fi = cantainer.GetType().GetField(item.name);
	      
			 
					fi.SetValue(cantainer, item);
 
			}

		}
		[ContextMenu("复制成代码声明")]
		private   void CopyToClip()
		{
       
			string msg = "";
			foreach (Object item in items)
			{
				msg += "public " + item.GetType().Name + " " + item.name + ";\n";
			}
			GUIUtility.systemCopyBuffer=msg;

		}

	}
}
