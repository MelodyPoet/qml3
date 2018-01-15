using System;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket
{
    public class SocketService : EventDispatcher
    {
        public static readonly SocketService instance = new SocketService();
        public Dictionary<int, Type> rspdClassDic= new Dictionary<int, Type>();
        private  EventDispatcher _eventDispatcher=new EventDispatcher();
        public void regRspdClass(int proID, Type rspdClass)
        {

            rspdClassDic[proID] = rspdClass;//.GetConstructor(new Type[0]);
        }
        public BaseRspd createRspdInstance(int proID,ByteArray bytes) {
            Type rspdCtr = null;
            BaseRspd rspd = null; 
            if (rspdClassDic.TryGetValue(proID, out rspdCtr))
            {
                Debug.Log("rspd makeing" + proID + ":" + rspdCtr);
                rspd = (BaseRspd)rspdCtr.Assembly.CreateInstance(rspdCtr.FullName);
                //  rspd = (BaseRspd)rspdCtr.Invoke(new object[0]);
                rspd.proID = proID;
                rspd.setData(bytes);
                rspd.readData();
                Debug.Log("rspd make end" + proID);
                return rspd;
            }
            else
            {
                Debug.Log("no rspd " + proID);
                return null;
            }

        }
    }
}
