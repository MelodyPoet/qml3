using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 namespace modules.passport.model
{
    public class PassportModel
    {
        public static PassportModel instance = new PassportModel();
		 public PassportService service;
        public bool hasRole = false;
        public string roleName="";
    }
}
