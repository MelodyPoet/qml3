package gang.gangBuild;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangBuildLikeRqst;
import protocol.GangBuildPVo;
import sqlCmd.AllSql;
import table.CommAwardBaseVo;
import table.GangUserDataEnum;
import table.ModuleAwardEnum;

/**
 * Created by admin on 2016/10/21.
 */
public class GangBuildLikeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangBuildLikeRqst rqst = (GangBuildLikeRqst) baseRqst;
        gangBuildLike(client,user,rqst.id);
    }

    private void gangBuildLike(Client client,User user,byte id){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(!gangVo.gangBuildMap.containsKey(id))return;
        GangBuildPVo buildPVo = gangVo.gangBuildMap.get(id);
        if(!gangVo.users.containsKey(user.guid))return;
        GangUserVo userVo = gangVo.users.get(user.guid);
        if(userVo.likeTime <= 0)return;
        userVo.likeTime--;
        AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_LIKE_TIME,userVo.likeTime);
        buildPVo.likes++;
        if(buildPVo.likes >= gangVo.maxLikes){
            if(buildPVo.likes >gangVo.maxLikes){
                gangVo.maxLikes = buildPVo.likes;
                for(GangBuildPVo pVo : gangVo.gangBuildMap.values()){
                    if(pVo.id == id)continue;
                    pVo.discount = 100;
                }
            }
            buildPVo.discount = (byte)Model.GameSetBaseMap.get(9).intArray[1];
        }
        CommAwardBaseVo vo = Model.CommAwardBaseMap.get((int)ModuleAwardEnum.GangLike);
        if(vo==null)return;
        gangVo.addExp(client,vo.awards[0]);
        userVo.addGangUserData(GangUserDataEnum.CONTRIBUTION,vo.awards[1],true);
        gangVo.createGangBuildInfoRspd(client);
        gangVo.saveGangBuildInfo();
    }
}
