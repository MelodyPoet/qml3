using System;

namespace starbucks.ui.basic
{
    public interface ILogic
    {
         void setGetLogicInModule(Func<Type, object> getLogic);
          void onInitView(BaseView view);
        void setModule(BaseModule baseModule);
        BaseModule getModule();
    }
}