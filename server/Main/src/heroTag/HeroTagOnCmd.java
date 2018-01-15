package heroTag;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.HeroTagOnRqst;
import protocol.HeroTagOnRspd;
import sqlCmd.AllSql;
import table.HeroTagBaseVo;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/2.
 */
public class HeroTagOnCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        HeroTagOnRqst rqst = (HeroTagOnRqst)baseRqst;
        if(rqst.id != 0){
            HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get((int) rqst.id);
            if (heroTagBaseVo == null) return;
            HashMap<Short, HeroTagVo> map = null;
            if (heroTagBaseVo.type == 1) {
                map = user.heroTagModel.zdlTagMap;
            } else {
                map = user.heroTagModel.activeTagMap;
            }
            if (!map.containsKey(rqst.id)) return;
            HeroTagVo heroTagVo = map.get(rqst.id);
            if (heroTagVo.status != 2) return;
        }

        user.cacheUserVo.heroTagID = rqst.id;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_HERO_TAG_ID,rqst.id);
        new HeroTagOnRspd(client,rqst.id);
    }
}
