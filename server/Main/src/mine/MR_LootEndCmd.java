package mine;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import mail.MailModel;
import protocol.*;
import sqlCmd.AllSql;
import table.MapTypeEnum;
import table.PropBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/12.
 */
public class MR_LootEndCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        MR_LootEndRqst rqst = (MR_LootEndRqst)baseRqst;
        int[] arr = Model.GameSetBaseMap.get(62).intArray;
        MineModel myModel = user.cacheUserVo.mineModel;
        if(rqst.type != myModel.type || rqst.index != myModel.index)return;
        if(rqst.success){
            if(rqst.type == MapTypeEnum.MINERAL_FIGHT){
                CacheUserVo cacheUserVo = myModel.indexMap.get(rqst.index);
                if(cacheUserVo == null)return;
                MineModel mineModel = cacheUserVo.mineModel;
                ArrayList<PropPVo> stoneList = new ArrayList<>();
                for(int i=mineModel.lostStoneCount;i<mineModel.lostStoneCount+mineModel.tempStoneCount;i++){
                    stoneList.add(mineModel.stoneList.get(i));
                }
                mineModel.lostMineCount += mineModel.tempMineCount;
                mineModel.lostStoneCount += mineModel.tempStoneCount;
                mineModel.protectTime = JkTools.getGameServerTime(null)+Model.GameSetBaseMap.get(65).intValue*60;
                mineModel.lootTime = 0;
                user.setUserData(UserDataEnum.MR_LOOT_COUNT,user.getUserData(UserDataEnum.MR_LOOT_COUNT)-1,true);
                user.addUserData(UserDataEnum.MR_MINE_COUNT,mineModel.tempMineCount,true);
                user.propModel.addListToBag(stoneList, ReasonTypeEnum.MR_SEARCH_PIT);
                if(!cacheUserVo.isRobot()){
                    mineModel.saveSqlData();
                    sendMail(user,cacheUserVo,mineModel.tempMineCount,stoneList);
                    MineLootVo mineLootVo = new MineLootVo();
                    mineLootVo.userID = cacheUserVo.guid;
                    mineLootVo.lostMine = mineModel.tempMineCount;
                    mineLootVo.lostStone = mineModel.tempStoneCount;
                    mineLootVo.createTime = JkTools.getGameServerTime(null);
                    AllSql.mineLootSql.insertNew(mineLootVo);
                    for(MineRecruitVo mineRecruitVo : mineModel.teamMap.values()){
                        AllSql.mineRecruitSql.delete(mineRecruitVo.guid);
                    }
                    mineModel.teamMap.clear();
                    mineModel.sendLootMsg = true;
                }
                myModel.indexMap.clear();
            }else{
                MineRoomVo roomVo = myModel.myRoom;
                if(roomVo == null)return;
                PublicMinePVo publicMinePVo = roomVo.publicMap.get(rqst.index);
                if(publicMinePVo == null || publicMinePVo.status == 0)return;
                int count = 0;
                if(rqst.type == -1){
                    count = arr[1];
                }
                if(rqst.type == MapTypeEnum.MINERAL_PUB_HIGH_FIGHT) {
                    count = arr[2];
                }
                if(rqst.type == MapTypeEnum.MINERAL_PUB_RARE_FIGHT) {
                    if(publicMinePVo.status < 0){
                        publicMinePVo.status = (byte)-publicMinePVo.status;
                    }
                    count = rqst.bossHurt*100/arr[5]*arr[4];
                }
                publicMinePVo.status --;
                if (publicMinePVo.status == 0) {
                    roomVo.publicMap.remove(rqst.index);
                }
                user.addUserData(UserDataEnum.MR_MINE_COUNT,count,true);
                }
        }else{
            if(rqst.type != MapTypeEnum.MINERAL_FIGHT){
                MineRoomVo roomVo = myModel.myRoom;
                PublicMinePVo publicMinePVo = roomVo.publicMap.get(rqst.index);
                if(publicMinePVo == null || publicMinePVo.status == 0)return;
                if(rqst.type == MapTypeEnum.MINERAL_PUB_RARE_FIGHT){
                    if(publicMinePVo.status < 0){
                        publicMinePVo.status = (byte)-publicMinePVo.status;
                    }
                    user.addUserData(UserDataEnum.MR_MINE_COUNT, rqst.bossHurt * 100 / arr[5] * arr[4], true);
                }
                publicMinePVo.status --;
                if (publicMinePVo.status == 0) {
                    roomVo.publicMap.remove(rqst.index);
                }
            }else{
                CacheUserVo cacheUserVo = myModel.indexMap.get(rqst.index);
                if(cacheUserVo == null)return;
                MineModel mineModel = cacheUserVo.mineModel;
                mineModel.lootTime = 0;
                if(!cacheUserVo.isRobot()){
                    user.setUserData(UserDataEnum.MR_LOOT_COUNT,user.getUserData(UserDataEnum.MR_LOOT_COUNT)-1,true);
                    MineLootVo mineLootVo = new MineLootVo();
                    mineLootVo.userID = cacheUserVo.guid;
                    mineLootVo.createTime = JkTools.getGameServerTime(null);
                    AllSql.mineLootSql.insertNew(mineLootVo);
                    for(MineRecruitVo mineRecruitVo : mineModel.teamMap.values()){
                        AllSql.mineRecruitSql.delete(mineRecruitVo.guid);
                    }
                    mineModel.teamMap.clear();
                    mineModel.sendLootMsg = true;
                }
                myModel.indexMap.clear();
            }
        }
//        if(rqst.type == MapTypeEnum.MINERAL_FIGHT){
//            if(GameSetModel.checkTime(15)){
//                new GoMapCmd().execute(client, user, 13817, true);
//            }else{
//                new GoMapCmd().execute(client, user, 1, true);
//            }
//        }else{
//            if(rqst.type == -1)return;
//            if(GameSetModel.checkTime(17)){
//                new GoMapCmd().execute(client, user, 13818, true);
//            }else{
//                new GoMapCmd().execute(client, user, 1, true);
//            }
//        }
    }

    public void sendMail(User user,CacheUserVo cacheUserVo,int mineCount,ArrayList<PropPVo> stoneList){
        MailPVo mailPVo = MailModel.createMail(10010,cacheUserVo.guid);
        StringBuffer sb = new StringBuffer();
        if(stoneList.size() > 0){
            for(PropPVo propPVo : stoneList){
                PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
                sb.append(","+propBaseVo.name+"*1");
            }
        }
        mailPVo.msg = mailPVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",mineCount+"").replace("{3}",sb.toString());
        if(cacheUserVo.onlineUser != null){
            cacheUserVo.onlineUser.mailModel.sendMail(mailPVo,true);
        }else{
            AllSql.mailSql.insertNew(mailPVo);
        }
    }
}
