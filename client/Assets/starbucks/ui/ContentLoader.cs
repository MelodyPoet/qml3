using System;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

namespace starbucks.ui
{
    public class ContentLoader : MonoBehaviour
    {
        public interface IContent {
            void onLoad();
            void onUnload();
        }
        public GameObject asset;
        public bool sharedAssets = false;
        [HideInInspector]
        public Component instance;
        [HideInInspector]
        public bool destroyMode=false;

        private static Dictionary<GameObject, Component> cacheSharedObjects=new Dictionary<GameObject, Component>();
        private Type bindingType;

        // Use this for initialization
        public object load (Type type) {
            if (sharedAssets)
            {
                if (cacheSharedObjects.ContainsKey(asset))
                {
                    instance = cacheSharedObjects[asset];
                    if (instance.transform.parent != transform)
                    {
                        instance.transform.SetParent(transform, false);
                        if (destroyMode == false) gameObject.SetActive(true);
                        // instance.transform.localPosition = Vector3.zero;
                        instance.transform.localScale = Vector3.one;
                        if (instance is IContent)
                        {
                            (instance as IContent).onLoad();
                        }
                    }
                    return instance ;
                }
            }
            if (instance==null)
            {
                instance = Instantiate<GameObject>(asset).AddComponent(type);    
            }
        
            if (sharedAssets)
            {
                if (cacheSharedObjects.ContainsKey(asset) == false)
                {
                    cacheSharedObjects[asset] = instance;
                }
            }

            instance.transform.SetParent(transform,false);

            if(destroyMode==false) gameObject.SetActive(true);
            // instance.transform.localPosition = Vector3.zero;
            instance.transform.localScale = Vector3.one;
            if (instance is IContent) {
                (instance as IContent).onLoad();
            }
            return instance  ; 
 
        }

        public T load<T>() where T : Component
        {
            return load(typeof(T)) as T;
        }

        public object load()
        {
            return load(bindingType);
        }

        public T getView<T>() where T : Component
        {
            if (instance != null) return instance as T;
       
            return null;

        }
        public void unload()
        {
            if (sharedAssets)
            {
                destroyMode = false;
            }
        
            if (instance == null)
            {
                return;
            }
            if (instance is IContent)
            {
                (instance as IContent).onUnload();
            }
            if (destroyMode)
                Destroy(instance.gameObject);
            else
                gameObject.SetActive(false);

        }

        public ContentLoader regComponent<T>(string autoUnloadEvent=null)
        {
            bindingType = typeof(T);
            if (autoUnloadEvent!=null)
            {
                starbucks.basic.EventDispatcher.globalDispatcher.AddEventListenerOnce(autoUnloadEvent,onAutoUnload);
            }
            return this;
        }

        private void onAutoUnload(EventData obj)
        {
            unload();
        }
    }
}
