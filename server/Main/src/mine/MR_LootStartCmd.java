package mine;

import base.GoMapCmd;
import comm.*;
import gameset.GameSetModel;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.*;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 2017/7/10.
 */
public class MR_LootStartCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MR_LootStartRqst rqst = (MR_LootStartRqst) baseRqst;
        ArrayList<SkillUserPVo> users = new ArrayList<>();
        ArrayList<PropPVo> stoneList = new ArrayList<>();
        int lostMineCount = 0;
        MineModel myModel = user.cacheUserVo.mineModel;
        myModel.index = rqst.index;
        myModel.type = rqst.type;
        int mapID = 0;
        if(rqst.type == -1){
            mapID = -1;
           if(!check(client,user,rqst.index,rqst.type,mapID))return;
        }
        if(rqst.type == MapTypeEnum.MINERAL_PUB_HIGH_FIGHT){
            Random random = new Random();
            int count = Model.GameSetBaseMap.get(67).intValue;
            mapID = Model.GameSetBaseMap.get(67).intArray[random.nextInt(count) + ((user.cacheUserVo.level-1)/10-2)*count];
            if(!check(client,user,rqst.index,rqst.type,mapID))return;
            lostMineCount = Model.GameSetBaseMap.get(62).intArray[2];
        }
        if(rqst.type == MapTypeEnum.MINERAL_PUB_RARE_FIGHT){
            mapID = 13901;
            if(!check(client,user,rqst.index,rqst.type,mapID))return;
            for(CacheUserVo team: user.cacheUserVo.mineModel.publicTeamMap){
                users.add(UserVoAdapter.toSkillUserPVo(team));
            }
        }
        if(rqst.type == MapTypeEnum.MINERAL_FIGHT){
            mapID = 13819;
            if(!GameSetModel.checkTime(15)){
                new ServerTipRspd(client,(short)359,null);
                return;
            }
            if(user.getUserData(UserDataEnum.MR_LOOT_COUNT)<=0)return;
            CacheUserVo cacheUserVo = user.cacheUserVo.mineModel.indexMap.get(rqst.index);
            if(cacheUserVo == null)return;
            MineModel mineModel = cacheUserVo.mineModel;
            int now = JkTools.getGameServerTime(null);
            int dTime = mineModel.endTime - now;
            if(dTime <= 0)return;
            int ltime = now - mineModel.lootTime;
            if(mineModel.lootTime != 0 && ltime < 0){
                new ServerTipRspd(client,(short)354,null);
                return;
            }
            int ptime = now - mineModel.protectTime;
            if(ptime <= 0){
                new ServerTipRspd(client,(short)353,null);
                return;
            }

            MineModel.calculate(now,mineModel,true);
            lostMineCount = mineModel.tempMineCount;

            for(int i=mineModel.lostStoneCount;i<mineModel.lostStoneCount+mineModel.tempStoneCount;i++){
                stoneList.add(mineModel.stoneList.get(i));
            }

            users.add(UserVoAdapter.toSkillUserPVo(cacheUserVo));
            for(MineRecruitVo team: cacheUserVo.mineModel.teamMap.values()){
                users.add(UserVoAdapter.toSkillUserPVo(team.cacheUserVo));
            }


            mineModel.lootTime = now+Model.MapBaseMap.get(mapID).timeLimit+10;
            user.activationModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON, MapTypeEnum.MINERAL_FIGHT);
        }
        new MR_LootStartRspd(client,rqst.index,users,lostMineCount,stoneList);
        if(mapID > 0){
            new GoMapCmd().execute(client, user, mapID, true);
        }
    }

    private boolean check(Client client,User user,byte index,byte type,int mapID){
        if(!GameSetModel.checkTime(17)){
            new ServerTipRspd(client,(short)359,null);
            return false;
        }
        MineRoomVo roomVo = user.cacheUserVo.mineModel.myRoom;
        PublicMinePVo publicMinePVo = roomVo.publicMap.get(index);
        if(publicMinePVo == null){
            new ServerTipRspd(client,(short)356,null);
            return false;
        }
        if(publicMinePVo.status == 1){
            new ServerTipRspd(client,(short)355,null);
            return false;
        }
        if(type == MapTypeEnum.MINERAL_PUB_RARE_FIGHT){
            publicMinePVo.status--;
            int count = Model.GameSetBaseMap.get(62).intArray[6];
            if(-publicMinePVo.status == count)publicMinePVo.status = (byte)-publicMinePVo.status;
        }else{
            publicMinePVo.status = 1;
        }
        if(type == -1){
            publicMinePVo.lootTime = JkTools.getGameServerTime(null) + Model.GameSetBaseMap.get(62).intArray[0]+10;
        }else{
            publicMinePVo.lootTime = JkTools.getGameServerTime(null) + Model.MapBaseMap.get(mapID).timeLimit+10;
        }
        return true;
    }
}
