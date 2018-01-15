package mine;

import base.BaseModel;
import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.MineralResBaseVo;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by admin on 2017/7/11.
 */
public class MR_GetLootTargetCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ArrayList<CacheUserVo> userList = new ArrayList<>();
        int now = JkTools.getGameServerTime(null);
        MineModel mineModel = user.cacheUserVo.mineModel;
        mineModel.indexMap.clear();
        byte index = 0;
        for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
            if(cacheUserVo.mineModel.endTime == 0 || now > cacheUserVo.mineModel.endTime)continue;
            if(cacheUserVo == user.cacheUserVo)continue;
            int d = cacheUserVo.level - user.cacheUserVo.level;
            if(d > 10 || d < -10)continue;
            userList.add(cacheUserVo);
        }
        ArrayList<UserMinePVo> list = new ArrayList<>();
        if(userList.size() > 15){
            HashSet<CacheUserVo> set = new HashSet<>();
            for(CacheUserVo cacheUserVo : userList){
                int d = cacheUserVo.level - user.cacheUserVo.level;
                if(d <= 5 || d >= -5) {
                    list.add(UserVoAdapter.toUserMinePVo(cacheUserVo,index));
                    mineModel.indexMap.put(index,cacheUserVo);
                    index++;
                    set.add(cacheUserVo);
                }
                if(list.size()>=15)break;
            }
            if(list.size()<15){
                for(CacheUserVo cacheUserVo : userList){
                    if(set.contains(cacheUserVo))continue;
                    list.add(UserVoAdapter.toUserMinePVo(cacheUserVo,index));
                    mineModel.indexMap.put(index,cacheUserVo);
                    index++;
                    if(list.size()>=15)break;
                }
            }
        }else if(userList.size() < 15){
            for(CacheUserVo cacheUserVo : userList){
                list.add(UserVoAdapter.toUserMinePVo(cacheUserVo,index));
                mineModel.indexMap.put(index,cacheUserVo);
                index++;
            }
            MineralResBaseVo mineralResBaseVo = Model.MineralResBaseMap.get(1);
            for(CacheUserVo cacheUserVo : MemoryRobot.allSantchUsers[user.cacheUserVo.level]){
                if(now > cacheUserVo.mineModel.endTime || cacheUserVo.mineModel.protectTime > 0){
                    robotMineInit(cacheUserVo.mineModel,mineralResBaseVo);
                }else{
                    int value = cacheUserVo.mineModel.endTime - now;
                    if(value <= 0) robotMineInit(cacheUserVo.mineModel,mineralResBaseVo);
                }
                list.add(UserVoAdapter.toUserMinePVo(cacheUserVo,index));
                mineModel.indexMap.put(index,cacheUserVo);
                index++;
                if(list.size() >= 15)break;
            }
        }
        new MR_GetLootTargetRspd(client,list);
        if(!Model.mineCampUsers.containsKey(user.guid)){
            Model.mineCampUsers.put(user.guid,user.cacheUserVo);
        }
    }

    private void robotMineInit(MineModel mineModel,MineralResBaseVo mineralResBaseVo){
        mineModel.mineType = 1;
        Random random = new Random();
        int min = random.nextInt(mineralResBaseVo.operateTime);
        mineModel.startTime = JkTools.getGameServerTime(null) - min*60;
        mineModel.endTime = mineModel.startTime + mineralResBaseVo.operateTime * 60;
        mineModel.protectTime = 0;
        ArrayList<PropPVo> mineList = BaseModel.getDropProps(mineralResBaseVo.drop[0],0);
        mineModel.total = 0;
        for(PropPVo propPVo : mineList){
            mineModel.total += propPVo.count;
        }
    }
}
