using UnityEngine;

namespace starbucks.uguihelp
{
    public class UguiRoot
    {
     public   static Canvas rootCanvas
        {
         get;
         private set;
        }

        public static  void init(Canvas canvas)
        {
            rootCanvas = canvas;
        }
        	public static bool setUIToWorldPos(RectTransform rectTransform,Vector3 wpos)
        {
            Vector2 pos;
            Vector3 inCmrPos = Camera.main.WorldToScreenPoint(wpos);
            if (inCmrPos.z < 0) return false;
          
            RectTransformUtility.ScreenPointToLocalPointInRectangle(rootCanvas.transform as RectTransform,inCmrPos , rootCanvas.worldCamera, out pos);
           
            rectTransform.anchoredPosition = pos;
            return true;
        }

        public static void resetMatix(RectTransform rectTf)
        {
            rectTf.anchorMin = Vector2.zero;
            rectTf.anchorMax = Vector2.one;
            rectTf.offsetMax = Vector2.zero;// = new Rect(0, 0, Screen.width, Screen.height);// Vector2.zero ;
            rectTf.offsetMin = Vector2.zero;
            rectTf.localScale = Vector3.one;
        }
    }

}