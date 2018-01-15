package gate;

import comm.BaseRqstCmd;
import comm.Client;
import gluffy.comm.BaseRqst;
import protocol.*;

public class ServerClientQuitCmd extends BaseRqstCmd {
    @Override
    public void execute(Client client, BaseRqst baseRqst) {
        ServerClientQuitRqst rqst = (ServerClientQuitRqst) baseRqst;

        client= (Client) Client.getOneAbs(rqst.clientID);
        if(client==null)return;

        if (client.hero.scene != null) {
            client.hero.scene.exit(client.hero);
        }
        Client.allOnline.remove(client.guid);
    }
}
