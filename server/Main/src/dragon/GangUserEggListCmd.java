package dragon;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.GangVo;
import gang.RankGangUserVo;
import gluffy.comm.BaseRqst;
import protocol.GangUserEggListRqst;
import protocol.GangUserEggListRspd;
import protocol.GangUserEggPVo;

import java.util.ArrayList;

/**
 * Created by admin on 2016/11/17.
 */
public class GangUserEggListCmd extends BaseRqstCmd{

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangUserEggListRqst rqst = (GangUserEggListRqst)baseRqst;
        getGangUserEggList(client,user,rqst.page);
    }

    private void getGangUserEggList(Client client,User user,byte page){
        ArrayList<GangUserEggPVo> userEggList = new ArrayList<>();
        if(user.cacheUserVo.gang.gangVo == null){
            new GangUserEggListRspd(client,0,userEggList);
            return;
        }
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(page<0)return;
        page = (byte) Math.min(page,gangVo.rankGangUserList.size()/DragonEggModel.SIZE);
        int start = page*DragonEggModel.SIZE;
        int i =0;
        user.cacheUserVo.dragonEggModel.nowGangUserEggSet.clear();
        for(RankGangUserVo rankGangUserVo: gangVo.rankGangUserList.list){
            if(rankGangUserVo.cacheUserVo.guid == user.guid)continue;
            i++;
            if(i>start){
                GangUserEggPVo pVo = new GangUserEggPVo();
                pVo.userID = rankGangUserVo.cacheUserVo.guid;
                pVo.name = rankGangUserVo.cacheUserVo.name;
                pVo.level = rankGangUserVo.cacheUserVo.level;
                pVo.portrait = rankGangUserVo.cacheUserVo.portrait;
                pVo.office = rankGangUserVo.office;
                pVo.eggList = user.cacheUserVo.dragonEggModel.getEggPVoList(rankGangUserVo.cacheUserVo);
                userEggList.add(pVo);
                user.cacheUserVo.dragonEggModel.nowGangUserEggSet.add(pVo.userID);
                System.out.println("======ADD_NOW_USER_LIST====="+pVo.userID);
                user.cacheUserVo.dragonEggModel.theirsEggUpSet.clear();
            }
            if(userEggList.size() >= DragonEggModel.SIZE)break;
        }
        new GangUserEggListRspd(client,gangVo.rankGangUserList.size()-1,userEggList);
    }
}
