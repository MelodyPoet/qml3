using System;
using UnityEngine;

namespace starbucks.socket
{
    public class CoreLibCallBack
    {
  
        public static Action<bool> OnSendingState;
       
        public static  Action<int>  OnShowError;
        public static  Action<int>  OnCloseAlert;
        public static bool isReourcesLoadMode=false;
        public static string localResPath;
        public static string persistentDataPath
        {
            get
            {
                if (isReourcesLoadMode)
                    return localResPath;
                else
                    return Application.persistentDataPath;
            }
        }
    }
}