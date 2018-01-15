using System;
using System.Collections;
using starbucks.ui.basic;
using UnityEngine;

namespace starbucks.uguihelp
{
    public class DataGroup : MonoBehaviour
    {
        public IList array;
        public BaseItemRender[] views;
        private Type itemType;
        public DataGroup init(IList array = null, Type itemType = null, IList itemAry = null,Action<BaseItemRender,object> selectAction=null, Action<BaseItemRender, object> renderAction=null)
        {
            this.array = array;
            this.itemType = itemType;
            views = new BaseItemRender[transform.childCount];

            //Debug.LogError("--DataGroup脚本 ---"+views);

            if (itemType == null)
            {
                for (int i = 0; i < transform.childCount; i++)
                {
                    views[i] = transform.GetChild(i).GetComponent(typeof(BaseItemRender)) as BaseItemRender;
                    views[i].index = i;
                    views[i].selectAction = selectAction;
                    views[i].renderAction = renderAction;
                }
            }
            else
            {
                for (int i = 0; i < transform.childCount; i++)
                {
                    views[i] = transform.GetChild(i).gameObject.AddComponent(itemType) as BaseItemRender;
                    views[i].index = i;
                    views[i].selectAction = selectAction;
                    views[i].renderAction = renderAction;
                }

            }
            if (itemAry != null)
            {
                for (int i = 0; i < itemAry.Count; i++)
                {

                    views[i] = itemAry[i] as BaseItemRender;
                    views[i].index = i;
                    views[i].selectAction = selectAction;
                    views[i].renderAction = renderAction;
                }
            }
   
         

            return this;
        }
    
        public void addClassInChild()//为了不限子类数量添加脚本
        {
            views = new BaseItemRender[transform.childCount];
            for (int i = 0; i < transform.childCount; i++)
            {
                views[i] = transform.GetChild(i).GetComponent(itemType) as BaseItemRender;
                if (views[i] == null)
                    views[i] = transform.GetChild(i).gameObject.AddComponent(itemType) as BaseItemRender;
                views[i].index = i;
            }
        }

        public void upateInIndex(int index)
        {
            views[index].updateData(index < array.Count ? array[index] : null);
        }

        public void flushData(IList array)
        {
            this.array = array;
            //AddDynamic();
            flushData();
        }
        public void flushData()
        {
            if (views == null)
                return;
            if (array == null)
            {
                for (int i = 0; i < views.Length; i++)
                {
                    if (views[i] == null) continue;
                    views[i].updateData(null);
                }
            }
            else
            {
                int dataLen = array.Count;

                for (int i = 0; i < views.Length; i++)
                {
                    if (views[i] == null) continue;
                    views[i].updateData(i < dataLen ? array[i] : null);
                }


            }
        }
 

        public void revertViews()
        {
            Array.Reverse(views);
        }

 


    }

}

