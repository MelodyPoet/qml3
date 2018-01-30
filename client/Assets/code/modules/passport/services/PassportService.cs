using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using starbucks.basic;
using starbucks.ui.basic;

namespace modules.passport.services
{
    public class PassportService:BaseService
    {
        private PassportModel model
        {
            get { return ModulesManager.passport.model; }
        }
 

   
        
         public override void init()
        {
            base.init();
            dispatcher.AddEventListener(LoginRspd.PRO_ID,onLoginRspd);
            dispatcher.AddEventListener(PassportRoleUpdateRspd.PRO_ID,onPassportRoleUpdateRspd);
        }

        private void onLoginRspd(EventData eventData)
        {
           LoginRspd rspd= eventData.objVal as LoginRspd;
            model.hasRole = rspd.hasRole;
        }
        private void onPassportRoleUpdateRspd(EventData eventData)
        {
            PassportRoleUpdateRspd rspd= eventData.objVal as PassportRoleUpdateRspd;
            model.roleName = rspd.name;
            
        }
    }
}
