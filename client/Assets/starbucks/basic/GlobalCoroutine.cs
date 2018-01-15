using UnityEngine;

namespace starbucks.basic
{
	public class GlobalCoroutine : MonoBehaviour {

		public static GlobalCoroutine instance
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
