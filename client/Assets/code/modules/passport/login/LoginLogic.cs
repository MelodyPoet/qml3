 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
using  modules.passport.model;
using modules.scene.main;
using starbucks.ui;
using starbucks.ui.basic;
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
      view.RequestLoginClk = () =>
      {
          new HideViewCmd(ModuleEnum.PASSPORT).excute(); 
          GameScene.instance.loadScene();
          new ShowViewCmd(ModuleEnum.CityMainPage).excute();
      };
        //logic call view
		//view.updateXXX(XXX);

 
        }
 
       
     //  void RequestXXX(EventData e)
     //   {
     //   e.objVal,e.intVal;
     // }

       } 

 

}