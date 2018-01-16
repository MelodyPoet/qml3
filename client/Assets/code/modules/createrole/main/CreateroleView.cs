 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.createrole.main
{
    public class CreateroleView: BaseView
    {
        //view call logic
         public Action<string> RequestCreateClk;
         
        public override void Init()
        {
          
            UIEventListener.Get(transform.Find("btnOk").gameObject).onClick = (go) =>
            {
                RequestCreateClk(transform.Find("iptName").GetComponent<InputField>().text);
            };
        }
 
        
    }

}