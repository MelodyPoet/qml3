package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.UserChangePortraitRqst;
import sqlCmd.AllSql;


public class UserChangePortraitCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        UserChangePortraitRqst rqst= (UserChangePortraitRqst) baseRqst;
        user.cacheUserVo.portrait=rqst.baseID;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_PORTRAIT,user.cacheUserVo.portrait);

    }

	 

}
