 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.passport.main
{
    public class PassportView: BaseView
    {
        //view call logic
         public Action RequestOnTestClk;
         
        public override void Init()
        {
            UIEventListener.Get(transform.Find("Button").gameObject).onClick = (go) =>
            {
                RequestOnTestClk();
            };
        }
 
        
    }

}