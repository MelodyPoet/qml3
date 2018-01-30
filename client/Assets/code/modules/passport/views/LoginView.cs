 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using modules.passport.model;
using starbucks.basic;
using starbucks.socket.udp;
using starbucks.uguihelp;
using starbucks.ui;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.passport.views
{
    public class LoginView: BaseView<PassportModule,PassportPanel>
    {
        public PassportModel model = PassportModel.instance;

        public override void Awake()
        {
            base.Awake();
            UIEventListener.bindVoidClickAction(transform.Find("btnLogin"), onLoginClk);

        }

        private void onLoginClk()
        {
         int guid=int.Parse(transform.Find("iptUid").GetComponent<InputField>().text);
            UdpService.instance.connect(guid,"127.0.0.1",9091);
         
            dispatcher.AddEventListener(LoginRspd.PRO_ID,onLoginRspd);

            new LoginRqst("123").send();
        }
    

        private void onLoginRspd(EventData obj)
        {
           panel.Hide();
 
             
            if (model.hasRole == false)
            {
                new ShowViewCmd(ModuleEnum.Createrole).excute();
            }
        }
        
    }

}