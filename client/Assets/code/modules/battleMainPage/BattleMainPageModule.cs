using starbucks.basic;

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using starbucks.utils;
using modules.battleMainPage.main;
using modules.battleMainPage.model;
using starbucks.ui.basic;
namespace modules.battleMainPage
{
    public   class BattleMainPageModule : BaseModule
    {
          private BattleMainPageModel model = BattleMainPageModel.instance;
 
        public override void init()
        {
           base.init();
            model.service=new BattleMainPageService();
          RegLogic(new BattleMainPageLogic());
 
           
        }


 

    }

}