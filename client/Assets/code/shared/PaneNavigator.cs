using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using starbucks.ui;
using Debug = UnityEngine.Debug;

namespace a1.shared
{
    //界面导航工具 show的时候加入 返回时候调用showBack
    public class PaneNavigator
    {
 


        private List<int> pathList = new List<int>(); //所有打开过的界面

        public void insert(int uiID,params int[] clearLastID)
        {
            if (pathList.Count > 0)
            {
              int last=  pathList[pathList.Count - 1];
                if (last == uiID) return;
                new HideViewCmd(last).excute();
                if (clearLastID != null && Array.IndexOf(clearLastID, last) > -1)
                {
                    pathList.RemoveAt(pathList.Count - 1);
                }
            }
            pathList.Add(uiID);
            foreach (int i in pathList)
            {
                UnityEngine.Debug.LogError(i);
            }
        }

        public void showBack()
        {
            if (pathList.Count == 0) return;
            new HideViewCmd(pathList[pathList.Count - 1]).excute();
            pathList.RemoveAt(pathList.Count-1);
            if (pathList.Count > 0)
            {
                new ShowViewCmd(pathList[pathList.Count - 1]).excute();
            }
        }

    }
}
