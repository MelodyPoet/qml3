package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;

/**
 * Created by admin on 2017/4/5.
 */
public class CloseGameCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
//        if(user.rednessModel.myRoom != null){
//            RP_RoomExitRqst rqst = new RP_RoomExitRqst();
//            rqst.isMe = true;
//            new RP_RoomExitCmd().execute(client,user,rqst);
//        }
        System.out.println("=====================CloseGame!!!!!!!!");
    }
}
