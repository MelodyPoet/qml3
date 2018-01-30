package gate;


import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ServerClientQuitRqst;

public class ServerClientQuitCmd extends BaseRqstCmd {


    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ServerClientQuitRqst rqst = (ServerClientQuitRqst) baseRqst;

        client= (Client) Client.getOneAbs(rqst.clientID);
        if(client==null)return;

        Client.allOnline.remove(client.guid);
    }
}
