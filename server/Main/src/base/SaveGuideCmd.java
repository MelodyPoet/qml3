package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AwardShowRspd;
import protocol.PropPVo;
import protocol.SaveGuideRqst;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2016/9/6.
 */
public class SaveGuideCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        SaveGuideRqst rqst = (SaveGuideRqst) baseRqst;
        saveGuide(client,user,rqst.guide);
    }

    private void saveGuide(Client client,User user,String guide){
        if(user.guide.indexOf("fiveStarGo") >= 0 && guide.indexOf("fiveStarGet") >= 0){
            int[] arr = Model.GameSetBaseMap.get(61).intArray;
            user.propModel.addListToBag(arr, ReasonTypeEnum.FIVE_STAR);
            ArrayList<PropPVo> list = new ArrayList<>();
            for(int i=0;i<arr.length;i+=2){
                PropPVo propPVo = new PropPVo();
                propPVo.baseID = arr[i];
                propPVo.count = arr[i+1];
                list.add(propPVo);
            }
            new AwardShowRspd(client,list);
        }
        user.guide = guide;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_GUIDE,"'"+guide+"'");
    }
}
