using starbucks.basic;

namespace starbucks.ui.basic
{
    public class BaseService 
    {
        public EventDispatcher dispatcher =  EventDispatcher.globalDispatcher;

        internal object _baseModel;

   
     /*   public   BaseService(object baseModel)
        {
            _baseModel = baseModel;
            init();
        }*/

        public virtual void init()
        {
     
        }

         

        protected virtual void OnInitGameRspd(EventData eventData)
        {
           

        }
 
        protected virtual void OnResetGame(EventData eventData)
        {

        }
       

   

    }
}