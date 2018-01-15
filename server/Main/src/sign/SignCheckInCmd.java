package sign;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.SignCheckInRqst;
import protocol.SignCheckInRspd;
import table.ReasonTypeEnum;
import table.SignBaseVo;
import table.UserDataEnum;

import java.util.Calendar;


public class SignCheckInCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
		SignCheckInRqst rqst=(SignCheckInRqst) baseRqst;
        short dayID=(short)rqst.dayID;
        Calendar todayCd= JkTools.getCalendar();;
        todayCd.add(Calendar.HOUR_OF_DAY,client.addHours);
        if(user.signModel.checkedDays.contains(dayID))return;
        int today= todayCd.get(Calendar.DAY_OF_YEAR);
        if(rqst.dayID!=today){
         if(user.costUserDataAndProp(UserDataEnum.DIAMOND, Model.GameSetBaseMap.get(5).intValue, true, ReasonTypeEnum.SIGN)==false)return;
        }
        user.signModel.clearLastMonSave(todayCd);
        SignBaseVo signBaseVo= Model.SignBaseMap.get((int)dayID);
       user.propModel.addListToBag(signBaseVo.prop,ReasonTypeEnum.SIGN);
//        ArrayList<PropPVo> list = new ArrayList<>();
//        for(int i=0;i<signBaseVo.prop.length;i+=2){
//            PropPVo propPVo = new PropPVo();
//            propPVo.baseID = signBaseVo.prop[0];
//            propPVo.count = signBaseVo.prop[1];
//            list.add(propPVo);
//        }
//        new AwardShowRspd(client,list);
       user.signModel.checkedDays.add(dayID);
        user.signModel.saveSqlData();
        user.addUserData(UserDataEnum.LJ_SIGN_COUNT,1,true);
        new SignCheckInRspd(client,dayID);
    }

	 

}
