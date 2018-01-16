using starbucks.basic;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseKit : UIComponent
    {

        public EventDispatcher dispatcher = EventDispatcher.globalDispatcher;
     
        protected GlobalCoroutine globalCoroutine= GlobalCoroutine.instance;
        public virtual void Init()
        {
            
        }

        public override void Awake()
        {
            base.Awake();
            Init();
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