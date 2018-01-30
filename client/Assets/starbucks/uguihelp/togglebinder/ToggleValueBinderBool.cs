namespace starbucks.uguihelp.togglebinder
{
	public class ToggleValueBinderBool : ToggleValueBinderBase
	{

		public bool value;
 
		protected	 override void applyValue (bool sel) {

			if (radioMode)
			{
				model.setValue(key, value);
			}
			else
			{
				if (sel)
				{
					model.addSetValue(key, value);
				}
				else
				{
					model.removeSetValue(key, value);
				}
			}

		}
	}
}
