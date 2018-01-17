 
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
using System;
using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.battleMainPage.main
{
    public class BattleMainPageView: BaseView
    {
        //view call logic
       public Action RequestExit;
         
        public override void Init()
        {
            transform.Find("myHead").gameObject.AddComponent<BattleMyHeadKit>();
            transform.Find("monsterHead").gameObject.AddComponent<BattleMonsterHeadKit>();
            UIEventListener.Get(transform.Find("btnExit").gameObject).onClick=(go)=>
                {
                    RequestExit();
                }
            ;
        }
 
        
    }

}