using System.Collections;
using System.Collections.Generic;
using modules.city;
using modules.passport;
 using starbucks.ui.basic;
using UnityEngine;
using UnityEngine.UI;

namespace modules.cityMainPage.views
{

public class CityMyHeadView : BaseView<CityModule,CityPanel> {
    
    private CityModel model
    {
        get { return module.model; }
    }
    public override void Awake()
    {
        base.Awake();
  
        dispatcher.AddEventListener(PassportRoleUpdateRspd.PRO_ID,(e)=>{
        updateName();});
        
        updateName();
    }

    private void updateName()
    {
        transform.Find("txtName").GetComponent<Text>().text = ModulesManager.passport.model.roleName;
    }
}


}