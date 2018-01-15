package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangRankListRqst;
import protocol.GangRankListRspd;
import protocol.GangRankPVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/7/5.
 */
public class GangRankListCmd extends BaseRqstCmd{
    public static final byte PAGE_SIZE = 20;
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangRankListRqst rqst = (GangRankListRqst) baseRqst;
        getGangRankList(client,user,rqst.page);
    }

    private void getGangRankList(Client client,User user,int page){
        Gang.sort();
        int endIndex = page+PAGE_SIZE;
        if(page != 0)return;
        if(page > Gang.allGangList.size())return;
        if(Gang.allGangList.size()<(page+PAGE_SIZE)){
            endIndex = Gang.allGangList.size();
        }
        List<GangVo> gangVoList = Gang.allGangList.subList(page,endIndex);

        ArrayList<GangRankPVo> rankList = new ArrayList<>();
        for(int i=0;i<gangVoList.size();i++){
            GangVo gangVo = gangVoList.get(i);
            GangRankPVo gangRankPVo = new GangRankPVo();
            gangRankPVo.gangID = gangVo.gangID;
            gangRankPVo.gangName = gangVo.gangName;
            gangRankPVo.level = gangVo.level;
            gangRankPVo.rank = i;
            gangRankPVo.gangZdl = gangVo.zdl;
            gangRankPVo.masterID = gangVo.master.cacheUserVo.guid;
            gangRankPVo.masterName = gangVo.master.cacheUserVo.name;
            gangRankPVo.masterPortrait = gangVo.master.cacheUserVo.portrait;
            gangRankPVo.userCount = gangVo.users.size();
            gangRankPVo.limitCount = gangVo.maxUserCount;
            gangRankPVo.gangBadge =gangVo.portrait;
            rankList.add(gangRankPVo);
        }
        if(user.cacheUserVo.gang.gangVo == null){
            new GangRankListRspd(client,page,-1,rankList);
        }else{
            new GangRankListRspd(client,page,user.cacheUserVo.gang.gangVo.rank,rankList);
        }
    }
}
