 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Net.Sockets;
using starbucks.utils;
using  modules.passport.model;
using modules.scene.main;
using starbucks.basic;
using starbucks.socket;
using starbucks.socket.udp;
using starbucks.ui;
using starbucks.ui.basic;
using BaseRqst = starbucks.socket.udp.BaseRqst;

namespace  modules.passport.login
{
    public class LoginLogic : BaseLogic<LoginView>
    {
        private PassportModel model = PassportModel.instance;
       

        public LoginLogic():base(0)
        {
       

        }

        public override void onInitView(LoginView view)
        {
            base.onInitView(view);
            
		//view call logic
      view.RequestLoginClk = (guid) =>
      {
           
          UdpService.instance.connect(guid,"127.0.0.1",9091);
         
          dispatcher.AddEventListener(LoginRspd.PRO_ID,onLoginRspd);

          new LoginRqst("123").send();
      };
        //logic call view
		//view.updateXXX(XXX);

 
        }

        private void onLoginRspd(EventData obj)
        {
                     new HideViewCmd(ModuleEnum.PASSPORT).excute(); 
                      // GameScene.instance.loadScene();
                      // new ShowViewCmd(ModuleEnum.CityMainPage).excute();
            if (model.hasRole == false)
            {
                new ShowViewCmd(ModuleEnum.Createrole).excute();
            }
        }


        //  void RequestXXX(EventData e)
     //   {
     //   e.objVal,e.intVal;
     // }

       } 

 

}