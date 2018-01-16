 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
 
using starbucks.utils;
 using starbucks.ui.basic;
namespace  modules.createrole.main
{
    public class CreateroleLogic : BaseMainLogic<CreateroleView>
    {
        

        public CreateroleLogic():base(ModuleEnum.Createrole,"createRoleView","createrole","comm")
        {
       

        }

        public override void onInitView(CreateroleView view)
        {
            base.onInitView(view);
            view.RequestCreateClk=(uname)=>{
                    new RoleCreateRqst(uname).send();
                    hide();
            }
            ;
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