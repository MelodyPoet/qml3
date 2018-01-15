using System;
using System.Collections;
using starbucks.basic;
using starbucks.uguihelp;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseMainLogic<TView> : BaseLogic<TView>  where TView:BaseView
    {
        public sbyte moduleID;
        private string prefabName;
        private string[] refRes;
        public string moduleRes;
        private LoadStateEnum resState= LoadStateEnum.EMPTY;

        public AssetBundle mainAssetBundle
        {
            get;
            private set;
        }

        public BaseMainLogic(sbyte moduleID,string prefabName,string moduleRes,params  string [] refRes)
        {
            Debug.Log("BaseMainLogic");
            this.refRes = refRes;
            this.moduleRes = moduleRes;
            this.moduleID = moduleID;
            this.prefabName = prefabName;
                dispatcher.AddEventListener(ModuleEvent.SHOW_MAIN_VIEW,
                                onShowEvent);
        }
    
        private void onShowEvent(EventData eventData)
        {
            Debug.Log("onShowEvent");
            if (eventData.intVal == moduleID)
            {
              
                if (view != null)
                {
                    show(eventData.aryVal);
                }
                else
                {
                    glbCoroutine.StartCoroutine(loading(() =>
                    {
                        show(eventData.aryVal);
                    }));
                }

            }
        }
        private    IEnumerator loading(Action onLoad)
        {
    
            yield return  loadRes();
            mainAssetBundle = UIAssetBundleManager.getOne(moduleRes);
            onLoadCmpCall(mainAssetBundle.LoadAsset<GameObject>(prefabName));
  
            onLoad();
        }
        void onLoadCmpCall(GameObject asset)
        {
            //CpuDebuger.print ("Instantiate::");
            //asset.gameObject.SetActive(needShowAfterLoad);

            GameObject ui = GameObject.Instantiate<GameObject>(asset);


            ui.transform.SetParent(UguiRoot.rootCanvas.transform);
            (ui.transform as RectTransform).anchorMin = Vector2.zero;
            (ui.transform as RectTransform).anchorMax = Vector2.one;
            (ui.transform as RectTransform).offsetMax = Vector2.zero;// = new Rect(0, 0, Screen.width, Screen.height);// Vector2.zero ;
            (ui.transform as RectTransform).offsetMin = Vector2.zero;

            ui.transform.localScale = Vector3.one;
            view = ui.AddComponent<TView>();
            onInitView(view);

        }

        /* 主logic 负责创建模块内 其他logic的view */
        protected void createLogicView<TLogic,TView>(string prefabName,bool isDefaultHidden=true) where TView:BaseView  where  TLogic:ILogic
        {
            GameObject uiAsset = mainAssetBundle.LoadAsset<GameObject>(prefabName);
            uiAsset.SetActive( false);
            GameObject ui = GameObject.Instantiate<GameObject>( uiAsset);
            ui.transform.SetParent(view.transform);
            TView childView=  ui.AddComponent<TView>();
            childView.isDefaultHidden = isDefaultHidden;
            getLogicInModule<TLogic>().onInitView(childView);
        }

        #region 模块资源加载管理

        

       
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
                    yield return glbCoroutine.StartCoroutine(UIAssetBundleManager.load(refRes[i]));
                }
            }
            if(moduleRes!=null)   yield return glbCoroutine.StartCoroutine(UIAssetBundleManager.load(moduleRes));
          
          
            resState = LoadStateEnum.LOADED;
        }

        public void unloadRes()
        {

            if (resState != LoadStateEnum.LOADED)
            {
                throw new Exception("unloadRes:"+this+":when "+resState);
                return;
            }
            if (moduleRes != null)
            {
                UIAssetBundleManager.unload(moduleRes);
            }
            if (refRes != null)
            {
                for (int i = 0; i < refRes.Length; i++)
                {
                    UIAssetBundleManager.unload(refRes[i]);
                }
            }
            resState = LoadStateEnum.EMPTY;
        }
        #endregion
   
    }
}