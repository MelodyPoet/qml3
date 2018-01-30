using starbucks.uguihelp;
using UnityEngine.UI;
 
using starbucks.ui.basic;
namespace  modules.createrole.views
{
    public class CreateroleView: BaseView<CreateroleModule,CreaterolePanel>
    {
        public override void Awake()
        {
            base.Awake();


            UIEventListener.bindVoidClickAction(transform.Find("btnOk"), onCreateClk);
            //
           
        }
        private void onCreateClk(){
            var uname=transform.Find("iptName").GetComponent<InputField>().text;
            new RoleCreateRqst(uname).send();
            panel.Hide();
        }
 

    } 
        
    

}