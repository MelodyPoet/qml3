 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
using  modules.cityMainPage.model;
using starbucks.ui.basic;
namespace  modules.cityMainPage.main
{
    public class CityMainPageLogic : BaseMainLogic<CityMainPageView>
    {
        private CityMainPageModel model = CityMainPageModel.instance;
       

        public CityMainPageLogic():base(ModuleEnum.CityMainPage,"cityMainPageView","citymainpage","comm")
        {
       

        }

        public override void onInitView(CityMainPageView view)
        {
            base.onInitView(view);
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