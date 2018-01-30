using UnityEngine;
using UnityEngine.UI;

namespace starbucks.uguihelp.togglebinder
{
	[RequireComponent(typeof(Toggle))]
	public class ToggleValueBinderBase : MonoBehaviour
	{
		public ToggleBinderModel model;
		public string key;

		private Toggle toggle;
		protected bool radioMode;
		// Use this for initialization
		private void Awake()
		{
			toggle = GetComponent<Toggle>();
			radioMode = toggle.group != null;
			toggle.onValueChanged.AddListener( onValueChanged);
			onValueChanged(toggle.isOn);
		}

		private void onValueChanged(bool sel)
		{
			if (radioMode)
			{
				if (sel)
				{
					applyValue(sel);
				}
			}
			else
			{
				applyValue(sel);
			}
		}
 
		protected	 virtual void applyValue (bool sel) {
		
		}
	}
}
