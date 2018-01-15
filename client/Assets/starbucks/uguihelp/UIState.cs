using System;
using UnityEngine;

namespace starbucks.uguihelp
{

    public class UIState : MonoBehaviour
    {
        private string lastState;
        public String stateName = "state1";
        public GameObject[] showItems;
        public GameObject[] hideItems;
        public GameObject[] moveItems;
        public Vector3[] movePos;

        public void SetLastState(string ls)
        {
            SendMessage("OnSetLastState", ls, SendMessageOptions.DontRequireReceiver);
        }

        private void OnSetLastState(string ls)
        {
            lastState = ls;
        }

        public void SetBoolState(bool active)
        {
            foreach (GameObject item in showItems)
            {
                item.SetActive(active);
            }
            foreach (GameObject item in hideItems)
            {
                item.SetActive(!active);
            }
        }

        private void OnSelfChange(string stateName)
        {
            if (lastState == stateName)
                return;

            lastState = stateName;
            if (this.stateName != stateName)
                return;
            foreach (GameObject item in showItems)
            {
                item.SetActive(true);
            }
            foreach (GameObject item in hideItems)
            {
                item.SetActive(false);
            }
            for (int i = 0; i < moveItems.Length; i++)
            {
                (moveItems[i].transform as RectTransform) .anchoredPosition = movePos[i];
            }
        }

        private void OnSelfChangeRevet(string stateName)
        {

            if (this.stateName != stateName)
                return;
            SetBoolState(false);
        }

        public void ChangeToState(string stateName, bool revert = false)
        {
            if (revert)
            {
                SendMessage("OnSelfChangeRevet", stateName, SendMessageOptions.DontRequireReceiver);

            }
            else
            {
                SendMessage("OnSelfChange", stateName, SendMessageOptions.DontRequireReceiver);
            }
        }
    }
}
