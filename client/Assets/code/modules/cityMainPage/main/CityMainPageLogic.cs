 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
using  modules.cityMainPage.model;
using starbucks.basic;
using starbucks.ui;
using starbucks.ui.basic;
namespace  modules.cityMainPage.main
{
    public class CityMainPageLogic : BaseMainLogic<CityMainPageView>
    {
        private CityMainPageModel model = CityMainPageModel.instance;
       

        public CityMainPageLogic():base(ModuleEnum.CityMainPage,"cityMainPageView","citymainpage","comm")
        {
       dispatcher.AddEventListener(SceneEnterRspd.PRO_ID,onSceneEnterRspd);

        }

        private void onSceneEnterRspd(EventData eventData)
        {
            SceneEnterRspd rspd= eventData.data as SceneEnterRspd;
            if (rspd.sceneID == 1)
            {
                new ShowViewCmd(ModuleEnum.CityMainPage).excute();
            }
            else
            {
                hide();
            }
        }

        public override void onInitView(CityMainPageView view)
        {
            base.onInitView(view);
            view.RequestEnterScene = (mapid) => { new SceneEnterRqst(mapid).send(); };

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