namespace starbucks.uguihelp.togglebinder
{
	public class ToggleValueBinderInt : ToggleValueBinderBase
	{

		public int value;
		public bool listMode;

		protected	 override void applyValue (bool sel) {

			if (radioMode)
			{
				model.setValue(key, value);
			}
			else
			{
				if (listMode)
				{
					if (sel)
					{

						model.setListValue(key, value, 1);
					}
					else
					{
						model.setListValue(key, value, 0);
					}
				}
				else
				{
					
				}
			}

		}
	}
}
