using starbucks.basic;

namespace starbucks.ui.basic
{
    public class BaseService 
    {
        public EventDispatcher dispatcher =  EventDispatcher.globalDispatcher;
 
        

        public BaseService()
        {
            
            init();
        }

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