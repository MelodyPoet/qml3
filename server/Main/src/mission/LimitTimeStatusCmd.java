package mission;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.LimitTimeStatusRqst;
import table.MissionBaseVo;

/**
 * Created by admin on 2017/5/8.
 */
public class LimitTimeStatusCmd extends BaseRqstCmd{

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        LimitTimeStatusRqst rqst = (LimitTimeStatusRqst)baseRqst;
        if(user.limitTimeMap.containsKey(rqst.type)){
            MissionModel msModel = user.limitTimeMap.get(rqst.type);
            if(rqst.status == 1){
                for (MissionBaseVo vo : Model.MissionBaseMap.values()) {
                    if (vo.type != msModel.msType) continue;
                    if (msModel.completedList.contains(vo.ID)) continue;
                    int value = msModel.canComplete(vo);
                    if (value == -1 || value == -2) continue;
                    msModel.acceptedList.put(vo, value);
                }
                msModel.isEnd = true;
            }else if(rqst.status == 3){
                user.limitTimeMap.remove(msModel.msType);
                user.timeOutSet.add(msModel.msType);
            }
            MissionModel.sendLimitTimeInfo(user);
            msModel.saveSqlData();
        }
    }
}
