package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.WB_SpecialFightResultRspd;
import table.MapTypeEnum;


public class SpecialFightResultCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        if(user.currentMap.type == MapTypeEnum.WORLDBOSS){

       //    new WB_SpecialFightResultRspd(client,user.worldBossModel.attackPointOnceEnter,  JkTools.intArrayAsList(Model.getDataInRange(3,user.worldBossModel.attackPointOnceEnter)));
        }


    }


}
