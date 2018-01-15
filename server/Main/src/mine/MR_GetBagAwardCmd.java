package mine;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.GeneralSuccessRspd;
import protocol.MR_GetBagAwardRqst;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/10.
 */
public class MR_GetBagAwardCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MineModel mineModel = user.cacheUserVo.mineModel;
        int now = JkTools.getGameServerTime(null);
        if(now - mineModel.endTime < 0 || now - mineModel.lootTime < 0)return;
        if(mineModel.endTime > mineModel.mineEndTime){
            MineModel.calculate(mineModel.mineEndTime,mineModel,false);
            mineModel.total = mineModel.tempMineCount;
            ArrayList<PropPVo> list = new ArrayList<>();
            for(int i=0;i<mineModel.tempStoneCount;i++){
                list.add(mineModel.stoneList.get(i));
            }
            mineModel.stoneList = list;
        }
        user.addUserData(UserDataEnum.MR_MINE_COUNT,mineModel.total-mineModel.lostMineCount,true);
        for(int i=0;i<mineModel.lostStoneCount;i++){
            mineModel.stoneList.remove(0);
        }
        user.propModel.addListToBag(mineModel.stoneList, ReasonTypeEnum.MR_SEARCH_PIT);
        mineModel.total = 0;
        mineModel.stoneList = new ArrayList<>();
        mineModel.lostMineCount = 0;
        mineModel.lostStoneCount = 0;
        mineModel.startTime = 0;
        mineModel.endTime = 0;
        mineModel.mineType = 1;
        mineModel.protectTime = 0;
        mineModel.lootTime = 0;
        mineModel.teamMap.clear();
        mineModel.costMap.clear();
        for(long guid : mineModel.lootset){
            AllSql.mineLootSql.delete(guid);
        }
        mineModel.lootset.clear();
        mineModel.sendLootMsg = false;
        mineModel.saveSqlData();
        new GeneralSuccessRspd(client, MR_GetBagAwardRqst.PRO_ID);
    }
}
