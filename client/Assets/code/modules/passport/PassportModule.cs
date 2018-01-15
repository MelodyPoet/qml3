using starbucks.basic;

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using modules.passport.login;
using starbucks.utils;
using modules.passport.main;
using modules.passport.model;
using starbucks.ui.basic;
namespace modules.passport
{
    public   class PassportModule : BaseModule
    {
          private PassportModel model = PassportModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new PassportService();
          
          RegLogic(new PassportLogic());
            RegLogic(new LoginLogic());
           
        }


 

    }

}