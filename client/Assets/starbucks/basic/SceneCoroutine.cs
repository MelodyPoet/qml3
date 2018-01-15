using UnityEngine;

namespace starbucks.basic
{
	public class SceneCoroutine : MonoBehaviour {

		public static SceneCoroutine instance
		{
			get;
			private set;
		}

		// Use this for initialization
		void Awake ()
		{
			instance = this;
		}
 
	}
}
