package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GetGangAwardRqst;
import table.CommAwardBaseVo;
import table.ModuleAwardEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2016/7/6.
 */
public class GetGangAwardCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetGangAwardRqst rqst = (GetGangAwardRqst) baseRqst;
        getGangAward(client,user);
    }

    private  void getGangAward(Client client, User user){
        CommAwardBaseVo commAwardBaseVo = Model.CommAwardBaseMap.get((int)ModuleAwardEnum.Gang);
        if(user.getUserData(UserDataEnum.IsGetGangAward) == 1){
            user.propModel.addListToBag(commAwardBaseVo.awards, 0);
            user.setUserData(UserDataEnum.IsGetGangAward,0,true);
        }
    }
}
