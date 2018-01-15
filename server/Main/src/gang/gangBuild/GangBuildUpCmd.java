package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangBuildPVo;
import protocol.GangBuildUpRqst;
import protocol.GangSkillMsgPVo;
import protocol.MyGangSkillListRspd;
import sqlCmd.AllSql;
import table.GangbuildBaseVo;

import java.util.ArrayList;

/**
 * Created by admin on 2016/10/21.
 */
public class GangBuildUpCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangBuildUpRqst rqst = (GangBuildUpRqst)baseRqst;
        gangBuildUp(client,user,rqst.id);
    }

    private void gangBuildUp(Client client,User user,byte id){
        if(user.cacheUserVo.gang.gangVo == null)return;
        if(!user.cacheUserVo.gang.isDeputyOrMaster(user.guid))return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!gangVo.gangBuildMap.containsKey(id))return;
        GangBuildPVo pVo = gangVo.gangBuildMap.get(id);
        if(pVo.level >= gangVo.maxLevel)return;
        int cost = pVo.costData * pVo.discount / 100;
        if(gangVo.exp < cost)return;
        ArrayList<GangbuildBaseVo> list = Model.GangbuildBaseMap.get((int)id);
        if(list==null)return;
        GangbuildBaseVo vo;
        if(id == 1){
            vo = list.get(pVo.level);
        }else{
            vo = list.get(pVo.level+1);
        }
        if(vo == null)return;
        gangVo.addExp(client,-cost);
        switch (id){
            case 1:
                gangVo.level++;
                pVo.level=gangVo.level;
                AllSql.gangSql.update(gangVo,AllSql.gangSql.FIELD_LEVEL,gangVo.level);
                gangVo.maxUserCount += vo.effect[0];
                AllSql.gangSql.update(gangVo,AllSql.gangSql.FIELD_MAX_USER_COUNT,gangVo.maxUserCount);
                pVo.effect = vo.effect[0];
                pVo.costData = vo.costData;
                gangVo.createGangInfoRspd(client);
                break;
            case 2:
                pVo.level++;
                pVo.effect = vo.effect[0];
                pVo.costData = vo.costData;
                if(pVo.effect <= 0)break;
                for(int i : vo.effect){
                    if(!user.gangSkillModel.gangSkillMap.containsKey((byte)i))continue;
                    GangSkillMsgPVo gangSkillMsgPVo = user.gangSkillModel.gangSkillMap.get((byte)i);
                    gangSkillMsgPVo.isLock = 0;
                }
                new MyGangSkillListRspd(client,pVo.level,user.gangSkillModel.gangSkillList);
                user.gangSkillModel.saveSqlData();
                break;
            case 3:
                break;
            case 4:
                break;
        }
        for(GangBuildPVo buildPVo : gangVo.gangBuildMap.values()){
            buildPVo.likes = 0;
            buildPVo.discount = 100;
        }
        gangVo.createGangBuildInfoRspd(client);
        gangVo.saveGangBuildInfo();
    }
}
