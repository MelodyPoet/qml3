 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
using  modules.battleMainPage.model;
using starbucks.basic;
using starbucks.ui;
using starbucks.ui.basic;
namespace  modules.battleMainPage.main
{
    public class BattleMainPageLogic : BaseMainLogic<BattleMainPageView>
    {
        private BattleMainPageModel model = BattleMainPageModel.instance;
       

        public BattleMainPageLogic():base(ModuleEnum.BattleMainPage,"battleMainPageView","battlemainpage","comm","citymainpage")
        {
       

            dispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);

        }

        private void onSceneEnterRspd(EventData eventData)
        {
            SceneEnterRspd rspd= eventData.data as SceneEnterRspd;
            if (rspd.sceneID != 1)
            {
                new ShowViewCmd(ModuleEnum.BattleMainPage).excute();
            }
            else
            {
                hide();
            }
        }

        public override void onInitView(BattleMainPageView view)
        {
            base.onInitView(view);
            view.RequestExit = () => { new SceneEnterRqst(1).send();};
            //      createLogicView<xxxxLogic,xxxxView>("xxxView");
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