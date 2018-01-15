package arena;

import base.GoMapCmd;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ArenaFightStartRqst;
import protocol.ArenaFightStartRspd;
import rank.RankModel;
import sqlCmd.AllSql;
import table.MapTypeEnum;
import table.MissionConditionEnum;
import table.ReasonTypeEnum;
import table.UserDataEnum;
import utils.UserVoAdapter;


public class ArenaFightStartCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if (user.cacheUserVo.rankArenaVo == null) return;
        ArenaFightStartRqst rqst = (ArenaFightStartRqst) baseRqst;
        if (user.cacheUserVo.rankArenaVo.orderIndex == -2) {
            RankModel.rankArenaList.addEnd(user.cacheUserVo.rankArenaVo);
            AllSql.userSql.update(user, AllSql.userSql.FIELD_ARENA_RANK, user.cacheUserVo.rankArenaVo.orderIndex);
            user.arenaModel.bestRecord = user.cacheUserVo.rankArenaVo.orderIndex;
            user.arenaModel.saveSqlData();
        }
        RankUserArenaVo targetArenaVo = RankModel.rankArenaList.get(rqst.rankIndex);
        if (targetArenaVo == null || targetArenaVo.cacheUserVo.guid == user.guid) return;
        if (user.costUserDataAndProp(UserDataEnum.ARENA_FIGHT_COUNT, 1, true, ReasonTypeEnum.ARENA_FIGHT) == false) {
            return;
        }
        user.arenaModel.fightingTarget = targetArenaVo;
        user.arenaModel.cacheArenaFighting = new CacheArenaFighting();
        user.arenaModel.cacheArenaFighting.user1 = user.cacheUserVo;
        user.arenaModel.cacheArenaFighting.user2 = targetArenaVo.cacheUserVo;
        int fightTime = Model.MapBaseMap.get(1000).timeLimit;
        user.arenaModel.cacheArenaFighting.completedTime = System.currentTimeMillis() + fightTime * 1000 + 10000;
        ArenaModel.CacheArenaFightingList.add(user.arenaModel.cacheArenaFighting);

        new ArenaFightStartRspd(client, UserVoAdapter.toSkillUserPVo(targetArenaVo.cacheUserVo), fightTime);
        new GoMapCmd().execute(client, user, 1000, true);
        user.activationModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON, MapTypeEnum.ARENA);

    }


}
