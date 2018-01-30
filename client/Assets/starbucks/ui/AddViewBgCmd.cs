using starbucks.basic;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.ui
{
    public class AddViewBgCmd  : ICommand
    {

        private GameObject view;
        public AddViewBgCmd(GameObject view)
        {
            this.view = view;
        }

        public void excute()
        {
            Image img=       view.AddComponent<Image>();
            img.color = Color.clear;
            img.raycastTarget = true;
        }
    }

}