package rank;

import comm.BaseRqstCmd;
import comm.Client;
import comm.RankListItem;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
 import gluffy.utils.RankSortedList;
import mine.MineModel;
import protocol.*;

import table.RankEnum;

import java.util.ArrayList;
import java.util.HashSet;


public class RankCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RankRqst rqst = (RankRqst) baseRqst;
        RankSortedList<? extends RankListItem> rankList = null;

        int oldValue = 0;
        switch (rqst.rankType) {
            case RankEnum.ZDL:
                rankList = RankModel.rankZdlList;
                user.updateZDL();
//                oldValue = user.zdl;
//                user.zdl = user.calculateZDL();
//                if (oldValue != user.zdl) {
//                    AllSql.userSql.update(user, AllSql.userSql.FIELD_ZDL, user.zdl);
//                    //排序之前不能改变分数否则原来的数组就次序被打乱
//                    RankModel.rankZdlList.sortItem(user.cacheUserVo.rankVo, user.zdl);
//                    user.cacheUserVo.zdl = user.zdl;
//                    if(user.cacheUserVo.gang.gangVo != null){
//                        user.cacheUserVo.gang.gangVo.zdl +=user.zdl-oldValue;
//                    }
//                }
                break;
            case RankEnum.DUNGEON:
                rankList = RankModel.rankFbList;
                break;
            case RankEnum.ARENA:
                rankList = RankModel.rankArenaList;
                break;
            case RankEnum.WORMNEST:
                rankList = RankModel.rankWormNestList;
                break;
            case RankEnum.GangUSER:
                if(user.cacheUserVo.gang.gangVo == null)return;
                rankList = user.cacheUserVo.gang.gangVo.rankGangUserList;
                break;
            case RankEnum.REDNESS:
                rankList = RankModel.rankRednessList;
                break;
            case RankEnum.MINE:
                rankList = RankModel.rankMineList;
                break;
        }


        int startIndex = rqst.startRank;


//        //查看自己 尽量放中心
//
//        if(startIndex==-1) {
//            startIndex = user.cacheUserVo.rankVo.orderIndex-5;
//        }
        if (rankList == null) return;
        if (startIndex < 0) startIndex = 0;
        if (startIndex >= rankList.size() && rankList.size() >0) return;
        int myIndex = user.cacheUserVo.getRankIndex(rqst.rankType);
        int myScore = user.cacheUserVo.getRankScore(rqst.rankType);

        if(rqst.rankType > 100){
            ArrayList<MineRankPVo> users = new ArrayList<>();
            int count = rankList.size();
            if(count == 0){
                new MR_RankRspd(client, rqst.rankType, 0, users, -1,0,1);
            }
            HashSet<Long> gangSet = new HashSet<>();
            for(int i=0;i<startIndex;i++){
                RankListItem rVo = rankList.get(i);
                GangVo gangVo = rVo.cacheUserVo.gang.gangVo;
                if(gangVo == null)continue;
                if(gangSet.contains(gangVo.gangID))continue;
                gangSet.add(gangVo.gangID);
            }
            for (int i = 0; i < 10; i++) {
                RankListItem rVo = rankList.get(startIndex + i);
                if (rVo == null) break;
                MineRankPVo mineRankPVo = rVo.makeMineRankPVo();
                users.add(mineRankPVo);
                GangVo gangVo = rVo.cacheUserVo.gang.gangVo;
                if(gangVo == null)continue;
                if(gangSet.contains(gangVo.gangID))continue;
                mineRankPVo.gangMax = 1;
                gangSet.add(gangVo.gangID);
            }
            int needCount = 0;
            if(myIndex == -1){
                if(count < MineModel.RANK_COUNT){
                    needCount = 1;
                }else{
                    needCount = rankList.get(count-1).orderScore()+1;
                }
            }else if(myIndex == 0){
                needCount = -1;
            }else{
                needCount = rankList.get(myIndex-1).orderScore()+1;
            }
            new MR_RankRspd(client, rqst.rankType, startIndex, users, myIndex,myScore,needCount);
        }else{
            ArrayList<RankUserPVo> users = new ArrayList<>();
            if(rankList.size() == 0){
                new RankRspd(client, rqst.rankType, 0, users, -1,0);
            }
            for (int i = 0; i < 10; i++) {
                RankListItem rVo = rankList.get(startIndex + i);
                if (rVo == null) break;
                users.add(rVo.makeRankPvo(rqst.rankType));
            }

            new RankRspd(client, rqst.rankType, startIndex, users, myIndex,myScore);
        }
        // user.missionModel.progressBuyAct(MissionActEnum.RANK);


    }


}
