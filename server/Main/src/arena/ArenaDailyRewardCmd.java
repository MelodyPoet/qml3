package arena;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import sqlCmd.AllSql;



public class ArenaDailyRewardCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {

  if(user.cacheUserVo.rankArenaVo.lastDayIndex<0)return;
int rank=user.cacheUserVo.rankArenaVo.lastDayIndex;
        int[] data = Model.getDataInRange(1,rank+1);
        if(data != null){
            user.propModel.addListToBag(data,0);
        }
        user.cacheUserVo.rankArenaVo.lastDayIndex=-2;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_ARENA_AWARD_FLAG,-2);

    }
}
