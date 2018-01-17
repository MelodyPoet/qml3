 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.cityMainPage.main
{
    public class CityMainPageView: BaseView
    {
        //view call logic
       public Action<int> RequestEnterScene;
         
        public override void Init()
        {
            transform.Find("myHead").gameObject.AddComponent<CityMyHeadKit>();
            UIEventListener.Get(transform.Find("btnMap1").gameObject).onClick = (go) => { RequestEnterScene(2); };
             UIEventListener.Get(transform.Find("btnMap2").gameObject).onClick = (go) => { RequestEnterScene(4); };
            UIEventListener.Get(transform.Find("btnMap3").gameObject).onClick = (go) => { RequestEnterScene(35); };
        }
 
        
    }

}