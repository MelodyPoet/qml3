 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.basic;
using starbucks.uguihelp;
using starbucks.ui;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.battleMainPage.views
{
    public class BattleView: BaseView<BattleModule,BattlePanel>
    {
        //view call logic
     
        public override void Awake()
        {
            base.Awake();
      

            UIEventListener.bindVoidClickAction(transform.Find("btnExit"), onExitClick);
 

    }

    private void onExitClick()
        {
            new SceneEnterRqst(1).send();
        }


 
        
    }

}