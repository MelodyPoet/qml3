using System;
using starbucks.basic;
using starbucks.uguihelp;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BasePanel: UIComponent
    {
        public UIEventDispatcher dispatcher =  UIEventDispatcher.globalUIDispatcher;
        
        protected GlobalCoroutine glbCoroutine= GlobalCoroutine.instance;
         
        
        protected BaseModule _baseModule;
        protected TView createView<TView>(GameObject go) where  TView: BaseView
        {
            TView v=   go.AddComponent<TView>();
            v._basePanel = this;
            v._baseModule = _baseModule;
            
            return v;
        }
        
        protected TView createView<TView>(string prefabName,bool useAssetPosition=false) where  TView: UIComponent
        {
            GameObject uiAsset =_baseModule.mainAssetBundle.LoadAsset<GameObject>(prefabName);
                        
            // uiAsset.SetActive( !isDefaultHidden);
            GameObject ui = GameObject.Instantiate<GameObject>( uiAsset);
            ui.transform.SetParent(transform);
            if(useAssetPosition==false)
            UguiRoot.resetMatix(ui.transform as RectTransform);
            else
            {
                UguiRoot.resetMatix(ui.transform as RectTransform, uiAsset.GetComponent<RectTransform>());
            }
            TView childView= ui.AddComponent<TView>();
            //  childView.isDefaultHidden = isDefaultHidden;
            //   getLogicInModule<TLogic>().onInitView(childView);
            if (childView is BaseView)
            {
                (childView as BaseView)._basePanel = this;
                (childView as BaseView)._baseModule = _baseModule;
            }

            return childView;
        }
        public   void Init(BaseModule baseModule)
        {
            _baseModule = baseModule;
            Init();
        }
        public virtual void Init()
        {
        }

    
        public virtual void Show(params object[] args)
        {
            SetActive(true);
            
        }
        public virtual void Hide()
        {
            SetActive(false);
            _baseModule.destroyPanel();
        }
        protected  virtual void OnDestroy()
        {
            dispatcher.RemoveAllEventListeners(this);
        }
    }
    public class BasePanel<TModule> : BasePanel where TModule:BaseModule
    {
    
         

        protected TModule module
        {
            get { return (TModule)_baseModule ; }
        }
 
        

    }
}