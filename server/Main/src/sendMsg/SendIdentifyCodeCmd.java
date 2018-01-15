package sendMsg;

import comm.BaseRqstCmd;
import comm.CachePassportVo;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.SendIdentifyCodeRqst;
import protocol.SendIdentifyCodeRspd;
import sqlCmd.AllSql;

import static sendMsg.ClosedTestModel.*;
import static sendMsg.SendMsgModel.sendSMS;

/**
 * Created by admin on 2016/8/25.
 */
public class SendIdentifyCodeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SendIdentifyCodeRqst rqst = (SendIdentifyCodeRqst)baseRqst;
        sendIdentifyCode(client,user,rqst.telphone);
    }

    public void sendIdentifyCode(Client client, User user,String telphone){
        if(ClosedTestModel.IDENTIFY_SUCCESS.equals(client.passportVo.identifyCode)){
            new SendIdentifyCodeRspd(client,(byte)3);
            return;
        }
        boolean isMobileNo = isMobileNO(telphone);
        if(!isMobileNo){
            new SendIdentifyCodeRspd(client,(byte)1);
            return;
        }
        if(CachePassportVo.useTelphone.contains(telphone)){
            new SendIdentifyCodeRspd(client,(byte)2);
            return;
        }
        String identifyCode = createIdentifyCode(client);
        sendSMS(client,telphone,identifyCode);
        client.passportVo.telphone = telphone;
        AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_TELPHONE,telphone);
        client.passportVo.identifyCode = identifyCode;
        AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_IDENTIFY_CODE,identifyCode);
        client.passportVo.codeDeadTime = JkTools.getGameServerTime(client) + LIMIT_TIME;
        AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_CODE_DEAD_TIME,client.passportVo.codeDeadTime);
        new SendIdentifyCodeRspd(client,(byte)0);
    }

}
