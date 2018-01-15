 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using modules.passport.login;
using starbucks.utils;
using  modules.passport.model;
using starbucks.ui.basic;
namespace  modules.passport.main
{
    public class PassportLogic : BaseMainLogic<PassportView>
    {
        private PassportModel model = PassportModel.instance;
       

        public PassportLogic():base(ModuleEnum.PASSPORT,"passportMainView","passport","comm")
        {
       
        
        }

        public override void onInitView(PassportView view)
        {
            base.onInitView(view);
            createLogicView<LoginLogic,LoginView>("loginView");
            getLogicInModule<LoginLogic>().show();
            view.RequestOnTestClk=()=>{
                    getLogicInModule<LoginLogic>().show();
            }
            ;
            // getLogicInModule<LoginLogic>().onInitView(GameObject.Instantiate(  mainAssetBundle.LoadAsset<GameObject>("loginView")).AddComponent<LoginView>());
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