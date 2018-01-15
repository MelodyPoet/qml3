package mine;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gameset.GameSetModel;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.MR_SearchPitRspd;
import protocol.ServerTipRspd;
import table.ReasonTypeEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2017/7/4.
 */
public class MR_SearchPitCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(!GameSetModel.checkTime(15)){
            new ServerTipRspd(client,(short)359,null);
            return;
        }
        MineModel mineModel = user.cacheUserVo.mineModel;
        if(mineModel.endTime != 0)return;
        int[] arr = Model.GameSetBaseMap.get(52).intArray;
        int freeCount = user.getUserData(UserDataEnum.MR_SEARCH_COUNT);
        if(freeCount > 0){
            user.setUserData(UserDataEnum.MR_SEARCH_COUNT,--freeCount,true);
        }else{
            user.costUserDataAndPropList(Model.GameSetBaseMap.get(56).intArray,true,ReasonTypeEnum.MR_SEARCH_PIT, null);
        }
        int index = JkTools.getRandRange(arr,10000,2);
        mineModel.mineType = (byte) arr[index+1];
        mineModel.saveSqlData();
        new MR_SearchPitRspd(client,mineModel.mineType);
    }
}
