package heroTag;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.HeroTagActiveRqst;
import protocol.HeroTagActiveRspd;
import protocol.HeroTagPVo;
import sqlCmd.AllSql;
import table.HeroTagBaseVo;
import table.MissionBaseVo;
import table.MissionTypeEnum;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/2.
 */
public class HeroTagActiveCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        HeroTagActiveRqst rqst = (HeroTagActiveRqst)baseRqst;
        HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get((int)rqst.id);
        if(heroTagBaseVo == null)return;
        HashMap<Short,HeroTagVo> map = null;
        if(heroTagBaseVo.type == 1){
            map = user.heroTagModel.zdlTagMap;
        }else{
            map = user.heroTagModel.activeTagMap;
        }
        HeroTagVo heroTagVo = null;
        if(map.containsKey(rqst.id)){
            heroTagVo = map.get(rqst.id);
            if(heroTagVo.status != 1)return;
            heroTagVo.status = 2;
            AllSql.heroTagSql.update(heroTagVo,AllSql.heroTagSql.FIELD_STATUS,heroTagVo.status);
        }else{
            MissionBaseVo missionBaseVo=Model.MissionBaseMap.get(heroTagBaseVo.missID);
            if(missionBaseVo==null)return;
            if(missionBaseVo.type != MissionTypeEnum.ACHIEVE)return;
            if(user.achieveModel.canComplete(missionBaseVo) >= 0)return;
            heroTagVo = new HeroTagVo();
            heroTagVo.userID = user.guid;
            heroTagVo.tagID = rqst.id;
            heroTagVo.status = 2;
            AllSql.heroTagSql.insertNew(heroTagVo);
            map.put(heroTagVo.tagID,heroTagVo);
        }

        HeroTagPVo heroTagPVo = new HeroTagPVo();
        heroTagPVo.id = heroTagVo.tagID;
        heroTagPVo.status = heroTagVo.status;
        heroTagPVo.endTime = heroTagVo.deadTime;
        user.updateZDL();
        new HeroTagActiveRspd(client,(byte) heroTagBaseVo.type,heroTagPVo);
    }
}
