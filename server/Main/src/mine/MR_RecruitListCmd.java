package mine;

import comm.*;
import friend.FriendModel;
import gameset.GameSetModel;
import gang.RankGangUserVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import gluffy.utils.RankSortedList;
import protocol.MR_RecruitListRqst;
import protocol.MR_RecruitListRspd;
import protocol.RecruitUserPVo;
import protocol.ServerTipRspd;
import table.UserDataEnum;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by admin on 2017/7/12.
 */
public class MR_RecruitListCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        if(!GameSetModel.checkTime(15)){
            new ServerTipRspd(client,(short)359,null);
            return;
        }
        MR_RecruitListRqst rqst = (MR_RecruitListRqst)baseRqst;
        MineModel mineModel = user.cacheUserVo.mineModel;
        mineModel.costMap.clear();
        ArrayList<RecruitUserPVo> list = new ArrayList<>();

        int listCount = 10;
        int freeCount = user.getUserData(UserDataEnum.MR_FREE_RECRUIT_COUNT);
        int startIndex = rqst.startRank;
        if(rqst.type == 1){
            Set<Long> myAttention = FriendModel.getMyAttention(user.guid);
            if(myAttention.size() == 0){
                new MR_RecruitListRspd(client,rqst.type,startIndex,list);
                return;
            }
            if(startIndex >= myAttention.size())return;
            ArrayList<CacheUserVo> myAttentionList = new ArrayList<>();
            for (long userID : myAttention) {
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
                if (cacheUserVo == null) continue;
                int index = -1;
                for(int i=0;i<myAttentionList.size();i++){
                    CacheUserVo userVo = myAttentionList.get(i);
                    if(cacheUserVo.zdl > userVo.zdl){
                        index = i;
                        break;
                    }
                }
                if(index == -1){
                    index = myAttentionList.size();
                }
                myAttentionList.add(index,cacheUserVo);
            }
            for(int i=0;i<myAttentionList.size();i++){
                if(i < startIndex)continue;
                CacheUserVo cacheUserVo = myAttentionList.get(i);
                RecruitUserPVo recruitUserPVo = UserVoAdapter.toRecruitUserPVo(cacheUserVo);
                int[] cost = getCost(user.cacheUserVo, cacheUserVo,freeCount);
                mineModel.costMap.put(cacheUserVo.guid, cost);
                recruitUserPVo.cost = JkTools.intArrayAsList(cost);
                recruitUserPVo.isRecruit = mineModel.used.contains(cacheUserVo.guid);
                list.add(recruitUserPVo);
                if (list.size() >= listCount) break;
            }
        }else{
            if (user.cacheUserVo.gang.gangVo != null){
                RankSortedList<RankGangUserVo> userList = user.cacheUserVo.gang.gangVo.rankGangUserList;
                if(userList.size() == 0){
                    new MR_RecruitListRspd(client,rqst.type,startIndex,list);
                    return;
                }
                if(startIndex >= userList.size())return;
                for(int i=0;i<userList.size();i++){
                    if(i < startIndex)continue;
                    CacheUserVo cacheUserVo = userList.get(i).cacheUserVo;
                    if(cacheUserVo.guid == user.guid)continue;
                    RecruitUserPVo recruitUserPVo = UserVoAdapter.toRecruitUserPVo(cacheUserVo);
                    int[] cost = getCost(user.cacheUserVo, cacheUserVo,freeCount);
                    mineModel.costMap.put(cacheUserVo.guid, cost);
                    recruitUserPVo.cost = JkTools.intArrayAsList(cost);
                    recruitUserPVo.isRecruit = mineModel.used.contains(cacheUserVo.guid);
                    list.add(recruitUserPVo);
                    if (list.size() >= listCount) break;
                }
            }
        }



//        if(list.size() < listCount){
//            for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
//                if(cacheUserVo.guid == user.guid)continue;
//                RecruitUserPVo recruitUserPVo = UserVoAdapter.toRecruitUserPVo(cacheUserVo);
//                int[] cost = getCost(user.cacheUserVo, cacheUserVo,freeCount);
//                mineModel.costMap.put(cacheUserVo.guid,cost);
//                recruitUserPVo.cost = JkTools.intArrayAsList(cost);
//                recruitUserPVo.isRecruit = mineModel.used.contains(cacheUserVo.guid);
//                list.add(recruitUserPVo);
//                if(list.size() >= listCount)break;
//            }
//        }

        new MR_RecruitListRspd(client,rqst.type,rqst.startRank,list);
    }

    private int[] getCost(CacheUserVo me,CacheUserVo userVo,int freeCount){
        int[] cost = {3,0};
        if(freeCount <= 0){
            if(userVo.level - me.level > 5 || userVo.zdl/me.zdl >=3){
                cost = Model.GameSetBaseMap.get(59).intArray;
            }else{
                int[] arr = Model.GameSetBaseMap.get(60).intArray;
                cost = new int[]{arr[0],arr[1]*Math.max((userVo.level-me.level),0)+arr[2]};
            }
        }
        return cost;
    }
}
