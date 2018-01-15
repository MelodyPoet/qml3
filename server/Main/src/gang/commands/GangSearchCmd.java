package gang.commands;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GangRankPVo;
import protocol.GangSearchRqst;
import protocol.GangSearchRspd;
import protocol.ServerTipRspd;

import java.util.ArrayList;

/**
 * Created by admin on 2016/10/13.
 */
public class GangSearchCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangSearchRqst rqst = (GangSearchRqst) baseRqst;
        gangSearch(client,user,rqst.page,rqst.gangName);
    }

    private void gangSearch(Client client,User user,byte page,String gangName){
        int pageSize = 10;
        if(page<0 && page>Gang.allGangList.size()/pageSize)return;
        int start = 0;
        boolean flag = false;
        ArrayList<GangRankPVo> gangList = new ArrayList<>();
        if("".equals(gangName)){
            for(GangVo gangVo : Gang.allGangList){
                if(gangVo.users.size() >= gangVo.maxUserCount)continue;
                if(start<page*pageSize){
                    start++;
                }else{
                    GangRankPVo pVo = new GangRankPVo();
                    pVo.gangID = gangVo.gangID;
                    pVo.gangName = gangVo.gangName;
                    pVo.gangZdl = gangVo.zdl;
                    pVo.level = gangVo.level;
                    pVo.masterID = gangVo.master.cacheUserVo.guid;
                    pVo.masterName = gangVo.master.cacheUserVo.name;
                    pVo.masterPortrait = gangVo.master.cacheUserVo.portrait;
                    pVo.zdl = gangVo.zdlLimit;
                    pVo.userCount = gangVo.users.size();
                    pVo.limitCount = gangVo.maxUserCount;
                    pVo.gangBadge = gangVo.portrait;
                    gangList.add(pVo);
                    if(gangList.size()>=pageSize)break;
                }
            }
            if(gangList.size()>0){
                new GangSearchRspd(client,page,gangList);
            }
        }else{
            for(GangVo gangVo : Gang.allGangList){
                if(gangVo.gangName.contains(gangName)){
                    flag = true;
                    if(start<page*pageSize){
                        start++;
                    }else {
                        GangRankPVo pVo = new GangRankPVo();
                        pVo.gangID = gangVo.gangID;
                        pVo.gangName = gangVo.gangName;
                        pVo.gangZdl = gangVo.zdl;
                        pVo.level = gangVo.level;
                        pVo.masterID = gangVo.master.cacheUserVo.guid;
                        pVo.masterName = gangVo.master.cacheUserVo.name;
                        pVo.masterPortrait = gangVo.master.cacheUserVo.portrait;
                        pVo.zdl = gangVo.zdlLimit;
                        pVo.userCount = gangVo.users.size();
                        pVo.limitCount = gangVo.maxUserCount;
                        pVo.gangBadge = gangVo.portrait;
                        gangList.add(pVo);
                        if(gangList.size()>=pageSize)break;
                    }
                }
            }
            if(gangList.size()>0){
                new GangSearchRspd(client,page,gangList);
            }else if(!flag){
                new ServerTipRspd(client,(short)131,null);
            }
        }
    }
}
