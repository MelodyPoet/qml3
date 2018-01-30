using System;
using starbucks.basic;
using starbucks.uguihelp;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseItemRender : UIComponent
    {
        public enum ClickModeEnum
        {
            NONE,CLICK,TOGGLE
        }
        public object data;
        public int index;
        [HideInInspector]
        public ClickModeEnum clickMode;
        public Action<BaseItemRender, object> selectAction;
        public Action<BaseItemRender, object> renderAction;
  
        public override void Start()
        {
            base.Start();
      
            if (selectAction != null)
            {
                switch (clickMode)
                {
                case ClickModeEnum.CLICK:
                
                    UIEventListener.Get(gameObject).onClick = (go) => {  selectAction(this, data);};
                    break;
                    
                    case ClickModeEnum.TOGGLE:
                       UIEventListener.Get(gameObject).onToggleChanged=(go,isSel) => {
                           if(isSel)
                           selectAction(this, data);
                           
                       };
                        break;
                }
                
            }
        }

        virtual public void updateData(object data)
        {
            this.data = data;
            if (renderAction != null)
            {
                renderAction(this, data);
            }
            else
            {
                if (typeof(BaseItemRender) == this.GetType())
                {
                    baseShowOrHide(data);
                }
            }
        }


        public    virtual     void baseShowOrHide(object data)
        {
          
                if (data == null)
                {
                    SetActive(false);
                    return;
                }
                SetActive(true);
               

           
        }
    }


}