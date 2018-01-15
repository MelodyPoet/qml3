using starbucks.basic;

namespace starbucks.ui.basic
{
    public class BaseView : UIComponent
    {
       public bool isDefaultHidden;
        public override void Awake()
        {
            base.Awake();
            if (isDefaultHidden) Init();
        }

        public virtual void Init()
        {
        }

    
        public virtual void Show(params object[] args)
        {
             SetActive(true);
            
        }
        public virtual void Hide()
        {
            SetActive(false);

        }



    }

}