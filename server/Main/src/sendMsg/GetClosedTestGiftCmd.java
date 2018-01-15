package sendMsg;

import airing.AiringModel;
import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.AiringMessagePVo;
import protocol.ClosedTestTimeRspd;
import protocol.GetClosedTestGiftRqst;
import protocol.GetClosedTestGiftRspd;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2016/8/25.
 */
public class GetClosedTestGiftCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetClosedTestGiftRqst rqst = (GetClosedTestGiftRqst)baseRqst;
        getClosedTestGift(client,user,rqst.identifyCode);
    }

    private void getClosedTestGift(Client client, User user,String identifyCode){
        if(user.isGetCloseTest == 1)return;
        if(user.hadGetTime >= ClosedTestModel.CAN_GET_TIME)return;
        if(user.hadGetTime<0){
            int time = JkTools.getGameServerTime(client) - client.passportVo.codeDeadTime;
            if(time>0){
                new GetClosedTestGiftRspd(client,(byte)2);
                return;
            }
            if(!client.passportVo.identifyCode.equals(identifyCode)){
                new GetClosedTestGiftRspd(client,(byte)1);
                return;
            }
            user.hadGetTime = 0;
            AllSql.userSql.update(user,AllSql.userSql.FIELD_HAD_GET_TIME,0);
            new ClosedTestTimeRspd(client,(byte)0,user.hadGetTime);
        }
        int id = 0;
        switch (user.hadGetTime){
            case 0 : id = (int)ModuleAwardEnum.ClosedTestGift; break;
            case 1 : id = (int)ModuleAwardEnum.ClosedTestGift1; break;
            case 2 : id = (int)ModuleAwardEnum.ClosedTestGift2; break;
        }
        CommAwardBaseVo vo = Model.CommAwardBaseMap.get(id);
        if(vo==null)return;
        user.propModel.addListToBag(vo.awards,ReasonTypeEnum.CLOSE_TEST_GIFT);
        if(user.hadGetTime == 0){
            client.passportVo.identifyCode = ClosedTestModel.IDENTIFY_SUCCESS;
            AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_IDENTIFY_CODE,ClosedTestModel.IDENTIFY_SUCCESS);
            CachePassportVo.useTelphone.add(client.passportVo.telphone);
        }
        user.isGetCloseTest = 1;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_IS_GET_CLOSE_TEST,user.isGetCloseTest);
        user.hadGetTime++;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_HAD_GET_TIME,user.hadGetTime);
        new ClosedTestTimeRspd(client,(byte)1,user.hadGetTime);
        new GetClosedTestGiftRspd(client,(byte)0);

        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(12);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        StringBuffer sb = new StringBuffer();
//        sb.append("恭喜 "+client.passportVo.name+" 领取了封测礼包，获得: ");
        for(int i=0;i<vo.awards.length;i+=2){
            PropBaseVo propBaseVo = Model.PropBaseMap.get(vo.awards[i]);
            if(propBaseVo==null)return;
            switch (propBaseVo.quality){
                case 0 :sb.append("[D1D1D1]");
                    break;
                case 1 :sb.append("[57E157]");
                    break;
                case 2 :sb.append("[57BCE1]");
                    break;
                case 3 :sb.append("[D248FF]");
                    break;
                case 4 :sb.append("[EC9141]");
                    break;
                case 5 :sb.append("[FF4848]");
                    break;
            }
            sb.append(propBaseVo.name+"[-]*"+vo.awards[i+1]+"、");
        }
        if(sb.length()>0)
        sb.deleteCharAt(sb.length()-1);
        pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",sb.toString());
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
