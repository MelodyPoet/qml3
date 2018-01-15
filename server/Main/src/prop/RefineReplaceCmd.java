package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import sqlCmd.AllSql;


public class RefineReplaceCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {


if(user.propModel.refinedItem==null&&user.propModel.refinedData==null)return;

        user.propModel.refinedItem.exAttribute=user.propModel.refinedData;
        AllSql.propSql.update(AllSql.propSql.FIELD_EXATTRIBUTERND, AllSql.propSql.exAttributeRndSqlData(user.propModel.refinedItem, true, null), user.propModel.refinedItem.tempID);

        user.propModel.refinedData=null;
        user.propModel.refinedItem=null;
        if(baseRqst!=null)
 new GeneralSuccessRspd(client,baseRqst.protocolID);
    }


}
