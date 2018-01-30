using System.Collections;
using System.Collections.Generic;
using modules.passport.model;
using starbucks.ui.basic;
using UnityEngine;
using UnityEngine.UI;

namespace modules.cityMainPage.views
{

public class CityMyHeadView : BaseView<CityModule,CityPanel> {
    public override void Awake()
    {
        base.Awake();
  
        dispatcher.AddEventListener(PassportRoleUpdateRspd.PRO_ID,(e)=>{
        updateName();});
        
        updateName();
    }

    private void updateName()
    {
        transform.Find("txtName").GetComponent<Text>().text = PassportModel.instance.roleName;
    }
}


}