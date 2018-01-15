using starbucks.basic;

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using starbucks.utils;
using modules.cityMainPage.main;
using modules.cityMainPage.model;
using starbucks.ui.basic;
namespace modules.cityMainPage
{
    public   class CityMainPageModule : BaseModule
    {
          private CityMainPageModel model = CityMainPageModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new CityMainPageService();
          RegLogic(new CityMainPageLogic());
 
           
        }


 

    }

}