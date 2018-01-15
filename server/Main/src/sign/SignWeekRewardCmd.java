package sign;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.SignWeekRewardRqst;
import protocol.SignWeekRewardRspd;
import table.ReasonTypeEnum;

import java.util.Calendar;


public class SignWeekRewardCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        SignWeekRewardRqst rqst= (SignWeekRewardRqst) baseRqst;
        //rqst.boxID;



byte weekID=(byte)rqst.weekID;
        if(weekID<=0)return;
         if(user.signModel.weekRewardSet.contains(weekID))return;
        Calendar todayCd= JkTools.getCalendar();
        todayCd.add(Calendar.HOUR_OF_DAY,client.addHours);
        Calendar weekFirst=JkTools.getCalendar();
        todayCd.add(Calendar.HOUR_OF_DAY,client.addHours);
        weekFirst.setWeekDate(todayCd.getWeekYear(),rqst.weekID-1,2);

        short weekFirstDay=(short)weekFirst.get(Calendar.DAY_OF_YEAR);
        for (short i = 0; i < 7; i++) {
            if(user.signModel.checkedDays.contains((short)(weekFirstDay+i))==false)return;
        }
        if( Model.SignWeekBaseMap.containsKey(rqst.weekID)==false)return;

        user.signModel.clearLastMonSave(todayCd);
        int[] propArr = Model.SignWeekBaseMap.get(rqst.weekID).prop;
        user.propModel.addListToBag(propArr, ReasonTypeEnum.SIGN);
//        ArrayList<PropPVo> list = new ArrayList<>();
//        for(int i=0;i<propArr.length;i+=2){
//            PropPVo propPVo = new PropPVo();
//            propPVo.baseID = propArr[0];
//            propPVo.count = propArr[1];
//            list.add(propPVo);
//        }
//        new AwardShowRspd(client,list);
       user.signModel.weekRewardSet.add(weekID);
        user.signModel.saveSqlData();
        new SignWeekRewardRspd(client,weekID);
    }

	 

}
