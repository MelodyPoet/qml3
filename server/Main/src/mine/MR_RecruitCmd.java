package mine;

import comm.*;
import gameset.GameSetModel;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.MR_RecruitRqst;
import protocol.MR_RecruitRspd;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;
import table.ReasonTypeEnum;
import table.UserDataEnum;
import utils.UserVoAdapter;

/**
 * Created by admin on 2017/7/12.
 */
public class MR_RecruitCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(!GameSetModel.checkTime(15)){
            new ServerTipRspd(client,(short)359,null);
            return;
        }
        MR_RecruitRqst rqst = (MR_RecruitRqst)baseRqst;
        if(rqst.index == 1 && client.passportVo.vip < Model.GameSetBaseMap.get(69).intValue)return;
        MineModel mineModel = user.cacheUserVo.mineModel;
        if(mineModel.teamMap.containsKey(rqst.index))return;
        if(!mineModel.costMap.containsKey(rqst.userID))return;
        if(mineModel.used.contains(rqst.userID))return;
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.userID);
        if(cacheUserVo == null)return;
        int count = user.getUserData(UserDataEnum.MR_FREE_RECRUIT_COUNT);
        if(count > 0){
            user.setUserData(UserDataEnum.MR_FREE_RECRUIT_COUNT,--count,true);
        }else{
            user.costUserDataAndPropList(mineModel.costMap.get(cacheUserVo.guid),true, ReasonTypeEnum.MR_RECRUIT,null);
        }
        mineModel.used.add(cacheUserVo.guid);
        MineRecruitVo mineRecruitVo = new MineRecruitVo();
        mineRecruitVo.index = rqst.index;
        mineRecruitVo.cacheUserVo = cacheUserVo;
        mineRecruitVo.createTime = JkTools.getGameServerTime(null);
        AllSql.mineRecruitSql.insertNew(mineRecruitVo);
        mineModel.teamMap.put(rqst.index,mineRecruitVo);
        mineModel.costMap.clear();
        mineModel.saveSqlData();
        new MR_RecruitRspd(client, UserVoAdapter.toTeamUserPVo(cacheUserVo,rqst.index));
    }
}
