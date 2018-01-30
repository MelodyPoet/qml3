﻿using System.Collections;
using System.Collections.Generic;
using modules.battleMainPage.model;
using modules.passport.model;
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
            transform.Find("txtName").GetComponent<Text>().text = PassportModel.instance.roleName;
        }
    }
}