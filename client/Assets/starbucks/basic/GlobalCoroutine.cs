using System;
using System.Collections;
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

		public void delayApply(float time,Action action)
		{
			StartCoroutine(delay(time, action));
		}

		private IEnumerator delay(float time,Action action)
		{
			yield return new WaitForSeconds(time);
			action();
		}
	}
}
