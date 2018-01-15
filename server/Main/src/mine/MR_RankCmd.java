package mine;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gang.Gang;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.MR_RankRqst;
import protocol.MR_RankRspd;
import protocol.MineRankPVo;
import protocol.RankRqst;
import rank.RankCmd;
import table.RankEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/24.
 */
public class MR_RankCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MR_RankRqst rqst = (MR_RankRqst)baseRqst;
        int myRank = -1;
        int myScore = 0;
        if(rqst.type == RankEnum.GANG_MINE){
            ArrayList<MineRankPVo> list = new ArrayList<>();
            for(GangVo gangVo : Gang.allGangList){
                int mineCount = 0;
                for(GangUserVo gangUserVo : gangVo.users.values()){
                    if(gangUserVo.cacheUserVo.rankMineVo != null){
                        mineCount += gangUserVo.cacheUserVo.rankMineVo.mineCount;
                    }
                }
                if(mineCount <= 0)continue;
                int index = -1;
                for(int i=0;i<list.size();i++){
                    if(mineCount > list.get(i).rankScore){
                        index = i;
                        break;
                    }
                }
                if(index == -1){
                    index = list.size();
                }
                MineRankPVo mineRankPVo = new MineRankPVo();
                mineRankPVo.guid = gangVo.gangID;
                mineRankPVo.name = gangVo.gangName;
                mineRankPVo.rankScore = mineCount;
                list.add(index,mineRankPVo);
            }
            for(int i=0;i<list.size();i++){
                MineRankPVo mineRankPVo = list.get(i);
                mineRankPVo.rankIndex = i;
                if(user.cacheUserVo.gang.gangVo == null)continue;
                if(mineRankPVo.guid == user.cacheUserVo.gang.gangVo.gangID){
                    myRank = i;
                    myScore = mineRankPVo.rankScore;
                }
            }
            getRankList(client,rqst.type,rqst.startRank,list,myRank,myScore);
        }else if(rqst.type == RankEnum.LAST_GANG_MINE){
            GangVo gangVo = user.cacheUserVo.gang.gangVo;
            if(gangVo != null){
                for(MineRankPVo mineRankPVo : MineModel.lastGangMineList){
                    if(mineRankPVo.guid == gangVo.gangID){
                        myRank = mineRankPVo.rankIndex;
                        myScore = mineRankPVo.rankScore;
                    }
                }
            }
            getRankList(client,rqst.type,rqst.startRank,MineModel.lastGangMineList,myRank,myScore);
        }else if(rqst.type == RankEnum.LAST_MINE){
            for(MineRankPVo mineRankPVo : MineModel.lastMineList){
                if(mineRankPVo.guid == user.guid){
                    myRank = mineRankPVo.rankIndex;
                    myScore = mineRankPVo.rankScore;
                }
            }
            if(myScore == 0){
                if(MineModel.lastUserMine.containsKey(user.guid)){
                    myScore = MineModel.lastUserMine.get(user.guid);
                }
            }
            getRankList(client,rqst.type,rqst.startRank,MineModel.lastMineList,myRank,myScore);
        }else {
            RankRqst rankRqst = new RankRqst();
            rankRqst.rankType = rqst.type;
            rankRqst.startRank = rqst.startRank;
            new RankCmd().execute(client,user,rankRqst);
        }
    }

    private void getRankList(Client client,byte rankType,int startIndex,ArrayList<MineRankPVo> rankList,int myRank,int myScore){
        if (startIndex < 0) startIndex = 0;
        int count = rankList.size();
        ArrayList<MineRankPVo> list = new ArrayList<>();
        if(count == 0){
            new MR_RankRspd(client,rankType,startIndex,list,myRank,myScore,1);
            return;
        }
        if (startIndex >= rankList.size()) return;
        for (int i = 0; i < 10; i++) {
            if(startIndex+i > rankList.size()-1)break;
            MineRankPVo mineRankPVo = rankList.get(startIndex + i);
            list.add(mineRankPVo);
        }
        int needCount = 0;
        if(myRank == -1){
            if(count < MineModel.RANK_COUNT){
                needCount = 1;
            }else{
                needCount = rankList.get(count-1).rankScore+1;
            }
        }else if(myRank == 0){
            needCount = -1;
        }else{
            needCount = rankList.get(myRank-1).rankScore+1;
        }
        new MR_RankRspd(client,rankType,startIndex,list,myRank,myScore,needCount);
    }
}
