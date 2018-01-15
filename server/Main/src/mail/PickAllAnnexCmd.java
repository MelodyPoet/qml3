package mail;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.MailPVo;
import protocol.PickAllAnnexRqst;
import table.ReasonTypeEnum;

import static mail.MailModel.getAnnexPropArr;

/**
 * Created by admin on 2016/7/27.
 */
public class PickAllAnnexCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        PickAllAnnexRqst rqst = (PickAllAnnexRqst) baseRqst;
        pickAllAnnex(client,user);
    }

    private void pickAllAnnex(Client client, User user){
        int[] propArr = new int[20];
        for(MailPVo mailPVo : user.mailModel.unReadMailMap.values()){
            pickAnnex(user,propArr,mailPVo.guid);
            if(mailPVo.isRead != 1){
                MailModel.readMail(user,mailPVo.guid,false);
            }
        }
        for(MailPVo mailPVo : user.mailModel.readMailMap.values()){
            pickAnnex(user,propArr,mailPVo.guid);
        }
        for(MailPVo mailPVo : user.mailModel.unReadMailMap.values()){
            user.mailModel.readMailMap.put(mailPVo.guid,mailPVo);
        }
        user.mailModel.unReadMailMap.clear();
        user.propModel.addListToBag(propArr, ReasonTypeEnum.MAIL);
    }

    private void pickAnnex(User user,int[] propArr,Long mailID){
        MailPVo mailPVo = user.mailModel.getMail(mailID);
        if(mailPVo == null)return;
        if(mailPVo.prop.size() > 0){
            int [] item = getAnnexPropArr(mailPVo.prop);
            for (int j =0;j<item.length;j+=2) {
                for(int z=0;z<propArr.length;z+=2){
                    if(item[j] == propArr[z]){
                        propArr[z+1] += item[j+1];
                        break;
                    }else if(propArr[z] == 0){
                        propArr[z] = item[j];
                        propArr[z+1] = item[j+1];
                        break;
                    }
                }
            }
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
