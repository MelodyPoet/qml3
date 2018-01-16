using System.Collections;
using System.Collections.Generic;
using modules.passport.model;
using starbucks.ui.basic;
using UnityEngine;
using UnityEngine.UI;

public class CityMyHeadKit : BaseKit {
    public override void Init()
    {
        base.Init();
        dispatcher.AddEventListener(PassportRoleUpdateRspd.PRO_ID,(e)=>{
        updateName();});
        updateName();
    }

    private void updateName()
    {
        transform.Find("txtName").GetComponent<Text>().text = PassportModel.instance.roleName;
    }
}
