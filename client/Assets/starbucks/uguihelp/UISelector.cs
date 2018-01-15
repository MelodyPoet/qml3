using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.uguihelp{
public class UISelector : MonoBehaviour
{
	public delegate bool CanOperateclk();
	public CanOperateclk canOperateClk;
    public GameObject[] groups;
    public GameObject[] items;
    public GameObject[] tabPanels;
    public List<GameObject> allItems;
    private GameObject selectedItem;
    public Vector3 scaleSelected = Vector3.one;
    public bool hasFlagItem = false;
    public GameObject sendMsgTo;
    public string sendMsgTag;
    public bool hideSelfOnFlagMode = false;
    public float quickClickTime = 0;
        private int autoSelect = -1;
    public GameObject SelectedItem
    {
        get
        {
            return selectedItem;
        }
        set
        {
            selectedItem = value;
        }
    }

    public int SelectedIndex
    {
        get
        {
            return allItems.IndexOf(SelectedItem);
        }
				 
    }

    void Awake()
    {
        if (sendMsgTo == null)
            sendMsgTo = gameObject;
        allItems = new List<GameObject>();
        foreach (GameObject item in groups)
        {
            for (int i = 0; i < item.transform.childCount; i++)
            {
                GameObject go = item.transform.GetChild(i).gameObject;
                allItems.Add(go);
					UIEventListener.Get(go).onClick = onClick;
                if (hasFlagItem)
                {
                    go.transform.Find("selFlag").gameObject.SetActive(false);
                        if (hideSelfOnFlagMode)
                        {
                            go.GetComponent<Graphic>().enabled = true;
                        }
                    }
            }

        }
				 
        foreach (GameObject item in items)
        {
				UIEventListener.Get(item).onClick = onClick;
            allItems.Add(item);
            if (hasFlagItem)
            {
                item.transform.Find("selFlag").gameObject.SetActive(false);
                if (hideSelfOnFlagMode)
                {
                  ////  item.GetComponent<UIWidget>().enabled = true;
                }
            }

						 
        }
            if (autoSelect != -1) selectItem(autoSelect);
    }

    public void updateItems()
    {

        foreach (GameObject item in groups)
        {
            for (int i = 0; i < item.transform.childCount; i++)
            {
                GameObject go = item.transform.GetChild(i).gameObject;
                if (allItems.Contains(go))
                    continue;//只适用顺序
                //if (allItems.Count == 0 || i >= allItems.Count/*||allItems[i]!=go*/)
                //{
                allItems.Add(go);

					 
					UIEventListener.Get(go).onClick = onClick;
                // }
                if (hasFlagItem)
                {
                    go.transform.Find("selFlag").gameObject.SetActive(false);
                        if (hideSelfOnFlagMode)
                        {
                            go.GetComponent<Graphic>().enabled = true;
                        }
                    }
            }
        }

        foreach (GameObject item in items)
        {
            
            if (allItems.Contains(item))
                continue;
            allItems.Add(item);
				UIEventListener.Get(item).onClick = onClick;
            if (hasFlagItem)
            {
                item.transform.Find("selFlag").gameObject.SetActive(false);
                if (hideSelfOnFlagMode)
                {
                    item.GetComponent<Graphic>().enabled = true;
                }
            }


        }
    }

    public void clearSelState()
    {
        if (allItems.Count > 0)
            allItems.Clear();
        selectedItem = null;
    }

    float lastClickTime = 0;

   public void onClick(GameObject go)
    {
        if (Time.time - lastClickTime < quickClickTime)
            return;
        lastClickTime = Time.time;
		//sometimes can't operate will still be selected
		if (canOperateClk != null&&canOperateClk()==false)
			return;
        if (selectedItem != null && selectedItem != go)
        {
            if (hasFlagItem)
            {
                selectedItem.transform.Find("selFlag").gameObject.SetActive(false);
                if (hideSelfOnFlagMode)
                {
                    selectedItem.GetComponent<Graphic>().enabled = true;
                }
            }
            selectedItem.SendMessage("OnCanceled", SendMessageOptions.DontRequireReceiver);
        }
        if (scaleSelected != Vector3.one && selectedItem != go)
        {
            go.transform.localScale = scaleSelected;
            if (selectedItem != null)
            {
                selectedItem.transform.localScale = Vector3.one;
            }
        }
        if (tabPanels.Length != 0)
        {
            if (selectedItem != null)
            {
                tabPanels[allItems.IndexOf(selectedItem)].gameObject.SetActive(false);
            }
            tabPanels[allItems.IndexOf(go)].gameObject.SetActive(true);
        }
        selectedItem = go;
        if (selectedItem != null)
        {
            if (hasFlagItem)
            {
                //Debug.LogError("selFlag");
                selectedItem.transform.Find("selFlag").gameObject.SetActive(true);
                if (hideSelfOnFlagMode)
                {
                    selectedItem.GetComponent<Image>().enabled = false;
                }
          
            }
            //	UISoundManager.instance.play(UISoundManager.instance.propSel);
            go.SendMessage("OnSelected", SendMessageOptions.DontRequireReceiver);
				
            sendMsgTo.SendMessage("OnSelected" + sendMsgTag, go, SendMessageOptions.DontRequireReceiver);
        }
        else
        {
            sendMsgTo.SendMessage("OnCanceledSelected" + sendMsgTag, go, SendMessageOptions.DontRequireReceiver);

        }
		 
    }

    public void selectItem(int i)
    {
            if (allItems.Count == 0)
            {
                autoSelect = i;
                return;
            }
        if (i == -1)
        {
            onClick(null);
        }
        else
        {
            onClick(allItems[i]);
        }
    }

    public void selectItemLast(int i)
    {
        if (allItems == null)
            return;
        onClick(allItems[allItems.Count - 1 - i]);
    }
}

}