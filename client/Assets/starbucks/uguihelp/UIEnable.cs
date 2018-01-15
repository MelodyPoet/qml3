using UnityEngine;

namespace starbucks.uguihelp
{
    public class UIEnable : MonoBehaviour
    {
        public GameObject maskItem;
        private bool _value = true;
        void Awake()
        {
            if (maskItem == null)
                return;
            maskItem.SetActive(false);
        }
        public bool Value
        {

            get
            {
                return _value;
            }
            set
            {

                if (_value == value)
                    return;
                _value = value;
                if (maskItem == null)
                    return;
                maskItem.SetActive(!_value);
                //Collider collider = GetComponent<Collider>();

                //collider.enabled = _value;


            }
        }
    }


}
