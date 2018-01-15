package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.*;


public class TypeTestCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        TypeTestRqst rqst= (TypeTestRqst) baseRqst;
//        System.out.println(rqst._byte);
//        System.out.println(rqst._short);
//        System.out.println(rqst._int);
//        System.out.println(rqst._long);
//        System.out.println(rqst._bool);
//        System.out.println(rqst._string);
        new TypeTestRspd(client,rqst._byte,rqst._short,rqst._int,rqst._long,rqst._bool,rqst._string);

    }


}
