 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.cityMainPage.main
{
    public class CityMainPageView: BaseView
    {
        //view call logic
       // public Action<EventData> RequestXXX;
         
        public override void Init()
        {
            transform.Find("myHead").gameObject.AddComponent<CityMyHeadKit>();
        }
 
        
    }

}