package mail;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.MailPVo;
import protocol.PickAnnexRqst;
import table.ReasonTypeEnum;

import static mail.MailModel.getAnnexPropArr;

/**
 * Created by admin on 2016/7/27.
 */
public class PickAnnexCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        PickAnnexRqst rqst = (PickAnnexRqst) baseRqst;
        pickAnnex(client,user,rqst.mailID);
    }

    public void pickAnnex(Client client, User user,Long mailID){
        MailPVo mailPVo = MailModel.readMail(user,mailID,true);
        if(mailPVo.prop.size() > 0){
            int [] item = getAnnexPropArr(mailPVo.prop);
            user.propModel.addListToBag(item, ReasonTypeEnum.MAIL);
            MailModel.delMailProp(mailPVo,user);
        }
//        if(mailPVo.money > 0){
//            user.addUserData(UserDataEnum.MONEY,mailPVo.money,true);
//            mailPVo.money = 0;
//            AllSql.mailSql.update(mailPVo,AllSql.mailSql.FIELD_MONEY,0);
//        }
//        if(mailPVo.diamond > 0){
//            user.addUserData(UserDataEnum.DIAMOND,mailPVo.diamond,true);
//            mailPVo.diamond = 0;
//            AllSql.mailSql.update(mailPVo,AllSql.mailSql.FIELD_DIAMOND,0);
//        }
    }
}
