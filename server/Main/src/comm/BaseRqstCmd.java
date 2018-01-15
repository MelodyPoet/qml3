package comm;

import gluffy.comm.AbsBaseRqstCmd;
import gluffy.comm.BaseRqst;

public abstract class BaseRqstCmd extends AbsBaseRqstCmd {
 	public abstract void execute(Client client,User user,BaseRqst baseRqst);
}
