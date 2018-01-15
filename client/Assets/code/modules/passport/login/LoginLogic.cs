 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
using  modules.passport.model;
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
        // view.RequestXXX = RequestXXX;
        //logic call view
		//view.updateXXX(XXX);

 
        }
 
       
     //  void RequestXXX(EventData e)
     //   {
     //   e.objVal,e.intVal;
     // }

       } 

 

}