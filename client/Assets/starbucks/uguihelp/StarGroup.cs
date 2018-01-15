using UnityEngine;

namespace starbucks.uguihelp
{
    public class StarGroup : MonoBehaviour
    {

        private int _lvl = 0;
        //  public bool autoHideParent=false;
        // Use this for initialization
        void Awake()
        {

            lvl = _lvl;
            //gameObject.SetActive(false);
        }

        public int lvl
        {
            get { return _lvl; }
            set
            {
                _lvl = value;

                for (int i = 0; i < transform.childCount; i++)
                {
                    transform.GetChild(i).gameObject.SetActive(i < _lvl);
                }
                //  if(autoHideParent)transform.parent.gameObject.SetActive(value>0);
            }
        }

    }
}

