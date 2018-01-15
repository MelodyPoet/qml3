using System;
using starbucks.basic;

namespace starbucks.ui.basic
{
    public class BaseItemRender : UIComponent
    {
        public object data;
        public int index;
        public Action<BaseItemRender, object> selectAction;
        public Action<BaseItemRender, object> renderAction;

        virtual public void updateData(object data)
        {
            this.data = data;
            if (renderAction != null)
            {
                renderAction(this, data);
            }
        }

        virtual public void updateSelState()
        {
        }

        virtual protected void OnSelected()
        {
            if (selectAction != null)
            {
                selectAction(this, data);
            }
        }

        virtual public void ShowEmpty(bool value)
        {
        }
    }


}