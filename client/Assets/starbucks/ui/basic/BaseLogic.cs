using System;
using starbucks.basic;
using starbucks.utils;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseLogic<TView> : ILogic where TView:BaseView
    {
        public EventDispatcher dispatcher =  EventDispatcher.globalDispatcher;
        internal TView view;
        protected GlobalCoroutine glbCoroutine= GlobalCoroutine.instance;
        private Func<Type, object> _getLogicInModule;
        public int uiID;
         private BaseModule module;


        public BaseLogic(int uiID=0)
        {
            this.uiID = uiID;
 

        }

        private void OnHideMainView(EventData eventData)
        {
            if (eventData.intVal == uiID)
            {
                hide();
            }
        }

        protected virtual void OnInitGameRspd(EventData eventData)
        {
           

        }
 
        protected virtual void OnResetGame(EventData eventData)
        {

        }

        public void onInitView(BaseView view)
        {
            onInitView(view as TView);
        }

        public void setModule(BaseModule baseModule)
        {
            module = baseModule;
        }

        public BaseModule getModule()
        {
            return module;
        }

        public virtual void onInitView(TView view)
        {

            this.view = view ;
            if (view.isDefaultHidden)
            {

            }
            else
            {
                view.Init();
            }

        }


  

        public virtual void show( object[] args=null)
        {
            view.Show(args);
          //  view.transform.SetAsLastSibling();
        }

        public virtual void hide()
        {
            if(view!=null)
            view.Hide();
        }

 

        public void setGetLogicInModule(Func<Type,object> getLogic)
        {
            _getLogicInModule = getLogic;
        }

        protected TLogic getLogicInModule<TLogic>()
        {
              return (TLogic)_getLogicInModule(typeof(TLogic));
        }
 
  
 
    }
}