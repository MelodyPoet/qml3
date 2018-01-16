using UnityEngine;

using System;
using System.Collections.Generic;

namespace starbucks.basic
{
    public class EventDispatcher
    {
        public static EventDispatcher globalDispatcher = new EventDispatcher();
        Dictionary<string, Action<EventData>> HandlerMap = new Dictionary<string, Action<EventData>>();
        Dictionary<Action<EventData>, string> OnceHandlerMap = new Dictionary<Action<EventData>, string>();
        public virtual void AddEventListener(string eventType, Action<EventData> action, bool callFirst = false)
        {
            if (HandlerMap.ContainsKey(eventType))
            {
                HandlerMap[eventType] -= action;
                if (callFirst)
                {
                    HandlerMap[eventType] = action + HandlerMap[eventType];
                }

                else
                {
                    HandlerMap[eventType] += action;
                }

            }
            else
            {

                HandlerMap[eventType] = action;
            }
        }
        public virtual void AddEventListener(int eventType, Action<EventData> action, bool callFirst = false)
        {
            AddEventListener(eventType + "", action, callFirst);
        }
        public virtual void AddEventListenerOnce(string eventType, Action<EventData> action, bool callFirst = false)
        {
            AddEventListener(eventType, action, callFirst);
            OnceHandlerMap[action] = eventType;
        }
        public virtual void AddEventListenerOnce(int eventType, Action<EventData> action, bool callFirst = false)
        {
            AddEventListenerOnce(eventType + "", action, callFirst);

        }

        public virtual void RemoveEventListener(string eventType, Action<EventData> action)
        {
            if (HandlerMap.ContainsKey(eventType))
            {
                Action<EventData> handler = HandlerMap[eventType];
                handler -= action;
                if (handler == null)
                {
                    HandlerMap.Remove(eventType);
                }
            }
        }
        public virtual void RemoveEventListener(int eventType, Action<EventData> action)
        {
            RemoveEventListener(eventType + "", action);
        }
        public virtual void RemoveAllEventListeners(string eventType)
        {
            if (HandlerMap.ContainsKey(eventType))
            {
                Action<EventData> handler = HandlerMap[eventType];
                handler = null;
                HandlerMap.Remove(eventType);

            }
        }
        public virtual void RemoveAllEventListeners()
        {
            HandlerMap.Clear();

        }
        public virtual EventData DispatchEvent(string eventType, int val)
        {
            EventData data = new EventData();
            data.eventType = eventType;
            data.intVal = val;
            return DispatchEvent(data);
        }
        public virtual EventData DispatchEvent(string eventType, string val)
        {
            EventData data = new EventData();
            data.eventType = eventType;
            data.strVal = val;
            return DispatchEvent(data);
        }
        public virtual EventData DispatchEvent(string eventType, params object[] val)
        {

            EventData data = new EventData();
            data.eventType = eventType;
            if (val.Length == 1)
                data.objVal = val[0];
            data.aryVal = val;
         return   DispatchEvent(data);
        }

        public virtual EventData DispatchEvent(EventData e)
        {
            if (HandlerMap.ContainsKey(e.eventType))
            {
                Action<EventData> handler = HandlerMap[e.eventType];
                if (handler != null)
                {
                    if (OnceHandlerMap.ContainsKey(handler))
                    {
                        RemoveEventListener(OnceHandlerMap[handler], handler);
                    }
                    handler(e);

                }
            }
            return e;

        }

 
    }
    public class EventData
    {
        public string eventType;
        public int intVal, intVal2, intVal3;
        public string strVal, strVal2, strVal3;
        public object objVal, objVal2, objVal3;
        public object[] aryVal;
        public object data {
            get { return objVal; }
        }
        public object   returnVal;
        public EventData(string eventType ,object val)
        {
            this.eventType = eventType;
            objVal = val;
        }
        public EventData(int val)
        {
            intVal = val;
        }
        public EventData(string val)
        {
            strVal = val;
        }
        public EventData(params object[] val)
        {
            if (val.Length == 1)
                objVal = val[0];
            aryVal = val;
        }

    }
}