 
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
        public Action<int> RequestLoginClk;
         
        public override void Init()
        {
            UIEventListener.Get(transform.Find("btnLogin").gameObject).onClick =(go)=>
            {
               
                RequestLoginClk(int.Parse( transform.Find("iptUid").GetComponent<InputField>().text));
            };
        }
 
        
    }

}