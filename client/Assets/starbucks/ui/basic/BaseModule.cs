using System;
using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using starbucks.uguihelp;
using starbucks.ui;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseModule 
    {
         private EventDispatcher dispatcher = EventDispatcher.globalDispatcher;
        private GlobalCoroutine glbCoroutine = GlobalCoroutine.instance;
        public sbyte moduleID;
        private string prefabName;
        private string[] refRes;
        public string moduleRes;
        private List<string> usingAssets=new List<string>();
        private LoadStateEnum resState= LoadStateEnum.EMPTY;
        public string showTabName;
        protected BasePanel _panel;
        protected Type panleType;
        public AssetBundle mainAssetBundle
        {
            get;
            private set;
        }

        public BaseModule()
        {
            init();
        }

        public virtual void init()
        {

        }
        protected TService RegService<TService>(object model) where TService:BaseService, new() 
        {
            TService s =   new TService();
            s._baseModel = model;
            s.init();
            return s;
        }
        protected void RegPanel <TPanle>(sbyte moduleID,string prefabName,string moduleRes,params  string [] refRes) where TPanle: BasePanel
        {
            Debug.Log("RegPanel");
            panleType = typeof(TPanle);
            this.refRes = refRes;
            this.moduleRes = moduleRes;
            this.moduleID = moduleID;
            this.prefabName = prefabName;
            dispatcher.AddEventListener(ModuleEvent.SHOW_MAIN_VIEW,
                onShowEvent);
            dispatcher.AddEventListener(ModuleEvent.HIDE_MAIN_VIEW,
                onHideEvent);
        }
        private void onHideEvent(EventData eventData)
        {
            //            Debug.Log("onShowEvent");
            if (eventData.intVal == moduleID)
            {
                if(_panel!=null)
                _panel.Hide();
            }
        }
        private void onShowEvent(EventData eventData)
        {
            // Debug.Log("onShowEvent");
            if (eventData.intVal == moduleID)
            {
                showTabName = eventData.strVal;
                /*     if (view != null)
                     {
                         show(eventData.aryVal);
                     }
                     else
                     {*/
                loadResAndShow(eventData.aryVal);
                //  }

            }
        }

        public void loadResAndShow(params object[] args)
        {
            glbCoroutine.StartCoroutine(loading(() =>
            {
                _panel.Show(args);
            }));
            
        }

        #region 模块资源加载管理
        public void destroyPanel()
        {
            if(_panel!=null)
                GameObject.Destroy(_panel.gameObject);
            unloadRes();
            _panel = null;
        }

        private    IEnumerator loading(Action onLoad)
        {
    
            yield return  glbCoroutine.StartCoroutine(loadRes());
            mainAssetBundle = AssetBundleManager.getOne("ui/"+moduleRes+".abd");
            if (_panel == null)
            {
                if (prefabName != null)
                {
                    onLoadCmpCall(mainAssetBundle.LoadAsset<GameObject>(prefabName));
                }
                else
                {
                    onLoadCmpCall(null);
                }
            }

            onLoad();
        }
        void onLoadCmpCall(GameObject asset)
        {
            //CpuDebuger.print ("Instantiate::");
            //asset.gameObject.SetActive(needShowAfterLoad);
            if (asset == null&& prefabName!=null)
            {
                throw new Exception("asset is null" +prefabName);
                return;
            }

            GameObject ui = null;
            if(prefabName!=null)
                ui=GameObject.Instantiate<GameObject>(asset);
            else
                ui=new GameObject(this.GetType().Name,typeof(RectTransform));
            


            ui.transform.SetParent(UguiRoot.rootCanvas.transform);
            UguiRoot.resetMatix(ui.transform as RectTransform);
            _panel = ui.AddComponent(panleType) as BasePanel;
             
            _panel.Init(this);
        }
        
        public   IEnumerator loadRes()
        {
            Debug.Log("loading");
          
            while (resState == LoadStateEnum.LOADING)
            {
                yield return null;
            }
            if (resState == LoadStateEnum.LOADED)
            {
                yield break;
            }
            resState = LoadStateEnum.LOADING;
    

            if (refRes != null)
            {
                for (int i = 0; i < refRes.Length; i++)
                {
                    yield return glbCoroutine.StartCoroutine(AssetBundleManager.load("ui/"+refRes[i]+".abd",usingAssets));
                }
            }
            if(moduleRes!=null)   yield return glbCoroutine.StartCoroutine(AssetBundleManager.load("ui/"+moduleRes+".abd",usingAssets));
          
          
            resState = LoadStateEnum.LOADED;
        }

        public void unloadRes()
        {

            if (resState != LoadStateEnum.LOADED)
            {
                // throw new Exception("unloadRes:"+this+":when "+resState);
                // return;
            }
            AssetBundleManager.unload(usingAssets);
            resState = LoadStateEnum.EMPTY;
        }
        #endregion
        
        
        }

 

      
    

}