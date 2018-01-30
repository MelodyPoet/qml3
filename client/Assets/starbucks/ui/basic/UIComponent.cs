using UnityEngine;

namespace starbucks.ui.basic
{
    public class UIComponent : MonoBehaviour
    {
        public virtual void Awake()
        {
        }

        public virtual void Start()
        {
        }

        //public virtual void Update()
        //{
        //}
        public virtual void OnEnable()
        {
        }
        public virtual void OnDisable()
        {
        }

        public virtual void SetActive(bool isActive)
        {
            this.gameObject.SetActive(isActive);
        }

    

        
    }
}