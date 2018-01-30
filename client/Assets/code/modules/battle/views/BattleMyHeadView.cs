using System.Collections;
 
using starbucks.ui.basic;
using UnityEngine;
using UnityEngine.UI;

namespace modules.battleMainPage.views
{

    public class BattleMyHeadView : BaseView<BattleModule, BattlePanel>
    {
        public override void Awake()
        {
            base.Awake();
    

            updateName();
        }

        private void updateName()
        {
            transform.Find("txtName").GetComponent<Text>().text = ModulesManager.passport.model.roleName;
        }
    }
}