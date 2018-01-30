 
using System;
using modules.passport.model;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
using UnityEngine;

namespace  modules.passport.views
{
    public class PassportView: BaseView<PassportModule,PassportPanel>
    {
        //view call logic
        public PassportModel model = PassportModel.instance;


        public override void Awake()
        {
            base.Awake();
            UIEventListener.bindVoidClickAction(transform.Find("Button"), RequestOnTestClk);
        }
 

        private void RequestOnTestClk()
        {
            
            panel.loginView.SetActive(true);
 
        }
    }

}