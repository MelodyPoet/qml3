using System;
using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

namespace starbucks.ui.basic
{
    public class BaseModule 
    {
         public EventDispatcher dispatcher = EventDispatcher.globalDispatcher;
         protected Dictionary<Type, object> logicDic=new Dictionary<Type,object>();

  
        public virtual void init()
        {

        }
        protected void RegLogic (ILogic logic){
            logicDic[logic.GetType()] = logic;
            logic.setModule(this);
            (logic as ILogic).setGetLogicInModule(getLogic);
        }

        private object getLogic(Type type)
        {
            return logicDic[type];
        }

        protected TLogic getLogic<TLogic>()  {
          
         
            return (TLogic) logicDic[typeof(TLogic)];
        }

      
    }

}