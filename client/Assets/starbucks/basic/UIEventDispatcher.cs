using System;
using System.Collections.Generic;
using UnityEngine;

namespace starbucks.basic
{
    public class UIEventDispatcher : EventDispatcher
    {
        public static UIEventDispatcher globalUIDispatcher = new UIEventDispatcher();

        Dictionary<object,Dictionary<string, Action<EventData> >> listeningDic=new Dictionary<object, Dictionary<string, Action<EventData>>>();
        public override void AddEventListener(string eventType, Action<EventData> action, bool callFirst = false)
        {
            base.AddEventListener(eventType, action, callFirst);
               Dictionary<string, Action<EventData> > dic=null;
                        if (listeningDic.ContainsKey(action.Target))
                        {
                            dic = listeningDic[action.Target];
                        }
                        else
                        {
                            dic = listeningDic[action.Target]=new Dictionary<string, Action<EventData>>();
                        }
            
                        dic[eventType] = action;
        }

        public   void RemoveAllEventListeners(MonoBehaviour target)
        {
            if (listeningDic.ContainsKey(target))
            {
               
                foreach (KeyValuePair<string, Action<EventData>> item in  listeningDic[target])
                {
                    RemoveEventListener(item.Key,item.Value);
                }
                listeningDic.Remove(target);
            }
        }

        public override EventData DispatchEvent(EventData e)
        {
            if (this.fromTransmitParent) return base.DispatchEvent(e);

            throw new Exception("ui事件派发器 只能接受 转发消息 不可派发，调用service内方法 执行派发");
          
        }

        public override EventData DispatchEvent(string eventType, int val)
        {
            if (this.fromTransmitParent)  return base.DispatchEvent(eventType, val);
            throw new Exception("ui事件派发器 只能接受 转发消息 不可派发，调用service内方法 执行派发");
           
        }

        public override EventData DispatchEvent(string eventType, params object[] val)
        {
            if (this.fromTransmitParent) return base.DispatchEvent(eventType, val);
            throw new Exception("ui事件派发器 只能接受 转发消息 不可派发，调用service内方法 执行派发");
             
        }

        public override EventData DispatchEvent(string eventType, string val)
        {
            if (this.fromTransmitParent) return base.DispatchEvent(eventType, val);
            throw new Exception("ui事件派发器 只能接受 转发消息 不可派发，调用service内方法 执行派发");
          
        }
    }
}