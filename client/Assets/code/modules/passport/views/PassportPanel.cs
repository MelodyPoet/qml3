using starbucks.ui.basic;

namespace modules.passport.views
{
    public class PassportPanel : BasePanel<PassportModule>
    {
        public PassportView passportView;
        public LoginView loginView;
        
        public override void Init()
        {
            base.Init();
 
            passportView= createView<PassportView>(gameObject);
            loginView= createView<LoginView>("loginView");
        }


    }
}