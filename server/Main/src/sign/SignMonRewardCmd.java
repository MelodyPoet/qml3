package sign;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.SignMonRewardRqst;
import protocol.SignMonRewardRspd;
import table.ReasonTypeEnum;
import table.SignBoxBaseVo;

import java.util.ArrayList;
import java.util.Calendar;


public class SignMonRewardCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user,BaseRqst baseRqst) {
        SignMonRewardRqst rqst= (SignMonRewardRqst) baseRqst;
        //rqst.boxID;
        Calendar todayCd= JkTools.getCalendar();
        todayCd.add(Calendar.HOUR_OF_DAY,client.addHours);
        short todayID=(short)todayCd.get(Calendar.DAY_OF_YEAR);
       byte boxID= rqst.boxID;
        if(user.signModel.boxGetDays.contains(boxID))return;
        int thisMon=todayCd.get(Calendar.MONTH);
        ArrayList<SignBoxBaseVo> boxList = Model.SignBoxBaseMap.get(thisMon+1);
        if(rqst.boxID-1<0||rqst.boxID-1>=boxList.size())return;

        SignBoxBaseVo boxBaseVo=  boxList.get(rqst.boxID-1);
        if(boxBaseVo.needDays>user.signModel.checkedDays.size())return;
        int checkedDays=0;
        Calendar saveCd=JkTools.getCalendar();
        for (short day : user.signModel.checkedDays){
            saveCd.set(Calendar.DAY_OF_YEAR,day);
            if(saveCd.get(Calendar.MONTH)==thisMon){
                checkedDays++;
            }
        }

if(boxBaseVo.needDays>checkedDays)return;
        user.signModel.clearLastMonSave(todayCd);
        user.propModel.addListToBag(boxBaseVo.prop, ReasonTypeEnum.SIGN);
//        ArrayList<PropPVo> list = new ArrayList<>();
//        for(int i=0;i<boxBaseVo.prop.length;i+=2){
//            PropPVo propPVo = new PropPVo();
//            propPVo.baseID = boxBaseVo.prop[0];
//            propPVo.count = boxBaseVo.prop[1];
//            list.add(propPVo);
//        }
//        new AwardShowRspd(client,list);
       user.signModel.boxGetDays.add(boxID);
        user.signModel.saveSqlData();
        new SignMonRewardRspd(client,boxID);
    }

	 

}
