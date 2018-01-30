using starbucks.basic;

namespace starbucks.ui.basic
{
    public class BaseView : UIComponent
    {
         public UIEventDispatcher dispatcher = UIEventDispatcher.globalUIDispatcher;
        protected GlobalCoroutine glbCoroutine = GlobalCoroutine.instance;
        internal BaseModule _baseModule;
        internal BasePanel _basePanel;

        protected  virtual void OnDestroy()
        {
            dispatcher.RemoveAllEventListeners(this);
        }
    }
    public class BaseView<TModule,TPanel> : BaseView where TModule:BaseModule  where TPanel: BasePanel
    {
    
         

        protected TModule module
        {
            get { return (TModule)_baseModule ; }
        }
        protected TPanel panel
        {
            get { return (TPanel) _basePanel; }
        }
        

    }


}