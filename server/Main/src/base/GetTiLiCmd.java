package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.GeneralSuccessRspd;
import protocol.GetTiLiRqst;
import sqlCmd.AllSql;
import table.ActivationBaseVo;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by admin on 2016/12/1.
 */
public class GetTiLiCmd extends BaseRqstCmd{

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetTiLiRqst rqst = (GetTiLiRqst)baseRqst;
        getTili(client,user,rqst.tempID,rqst.type);
    }

    private void getTili(Client client,User user,byte tempID,byte type){
        ArrayList<ActivationBaseVo> list = Model.ActivationBaseMap.get(6);
        if(list == null)return;
        ActivationBaseVo vo = list.get(tempID);
        if(vo == null)return;
        Calendar calendar = JkTools.getCalendar();
        calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(user.getTili.indexOf(String.valueOf(tempID)) != -1)return;
        if(type == 0){//免费领取体力
            if(vo.conditionParams!=null){
                if (JkTools.compare(hour, vo.conditionParams[1], vo.conditionParams[0]) == false)return;
                if (vo.conditionParams.length>=4&&JkTools.compare(hour, vo.conditionParams[3], vo.conditionParams[2]) == false)return;
            }
            user.propModel.addListToBag(vo.reward,ReasonTypeEnum.GET_TILI);
        }else{//钻石领取体力
            if(vo.conditionParams!=null && hour>=6){
                if (JkTools.compare(hour, vo.conditionParams[3], vo.conditionParams[0]) == false)return;
            }
            user.costUserDataAndPropList(vo.cost,true, ReasonTypeEnum.GET_TILI,null);
            user.propModel.addListToBag(vo.reward,ReasonTypeEnum.GET_TILI);
        }
        StringBuffer sb = new StringBuffer(user.getTili);
        if(sb.length() != 0){
            sb.append(",");
        }
        sb.append(tempID);
        user.getTili = sb.toString();
        user.activationModel.progressBuyAct(MissionConditionEnum.GET_TILI,tempID);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_GET_TILI,"'"+user.getTili+"'");
        new GeneralSuccessRspd(client,GetTiLiRqst.PRO_ID);
    }
}
