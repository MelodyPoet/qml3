using starbucks.basic;

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using starbucks.utils;
using modules.createrole.main;
 using starbucks.ui.basic;
namespace modules.createrole
{
    public   class CreateroleModule : BaseModule
    {
  
        public override void init()
        {
           base.init();
           RegLogic(new CreateroleLogic());
 
           
        }


 

    }

}