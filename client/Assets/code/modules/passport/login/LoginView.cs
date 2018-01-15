 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.passport.login
{
    public class LoginView: BaseView
    {
        //view call logic
        public Action RequestLoginClk;
         
        public override void Init()
        {
            UIEventListener.Get(transform.Find("btnLogin").gameObject).onClick =(go)=>
            {
                RequestLoginClk();
            };
        }
 
        
    }

}