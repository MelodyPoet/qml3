using starbucks.basic;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseKit : UIComponent
    {

        public EventDispatcher dispatcher = EventDispatcher.globalDispatcher;
     
        protected MonoBehaviour app ;
        public virtual void Init()
        {
            
        }

    
        //public virtual void Show(params object[] args)
        //{
        //     SetActive(true);
          
        //}
        //public virtual void Hide()
        //{
        //    SetActive(false);

        //}



    }

}