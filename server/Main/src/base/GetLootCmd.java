package base;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import elit.EliteModel;
import gang.map.MapModel;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import mail.MailModel;
import prop.PropModel;
import protocol.*;
import rank.RankModel;
import rank.RankUserFbVo;
import redness.RP_RoomExitCmd;
import redness.RP_RoomVo;
import redness.RednessModel;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;
import java.util.List;


public class GetLootCmd extends BaseRqstCmd {
    boolean mapAttackMode=false;
    public GetLootCmd() {}
    public GetLootCmd(boolean mapAttackMode) {
        this.mapAttackMode=mapAttackMode;
    }

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        if(user.currentMap.type == MapTypeEnum.NEWGAME_FIGHT){
            new GoMapCmd().execute(client, user, 1, true);
            return;
        }
        execute(client,user,baseRqst,true);

    }

    public void execute(Client client, User user, BaseRqst baseRqst, boolean needJumpMap) {
        if( user.actState == UserActState.CAMP)return;
        user.actState = UserActState.CAMP;
        GetLootRqst rqst = (GetLootRqst) baseRqst;
        if(user.currentMap!=null){
            if(user.currentMap.type==MapTypeEnum.WORLDBOSS){
                int attackValue =0;// user.worldBossModel.attackPointOnceEnter;
               int [] data= Model.getDataInRange(3,attackValue);
                if(user.cacheUserVo.isRobot())return;
                if(data!=null){
                    MailPVo mailPVo = MailModel.createMail(10006,user.guid);
                    ArrayList<AnnexPropPVo> list = new ArrayList<>();
                    for(int i=0;i<data.length;i+=2){
                        int id = data[i];
                        if(id<0)continue;
                        AnnexPropPVo pVo = new AnnexPropPVo();
                        pVo.propID = id;
                        pVo.count = data[i+1];
                        list.add(pVo);
                    }
                    mailPVo.prop = list;
                    mailPVo.msg = mailPVo.msg.replace("{1}",""+attackValue);
                  //  mailPVo.msg = mailPVo.msg.replace("{2}",""+(user.cacheUserVo.rankUserWorldBossVo.orderIndex+1));
                    user.mailModel.sendMail(mailPVo,true);
                }
                user.addUserData(UserDataEnum.LJ_WORLD_BOSS_HURT,attackValue/10000+attackValue%10000/5000,true);//四舍五入
                int max = user.getUserData(UserDataEnum.MAX_WORLD_BOSS_HURT);
                if(attackValue > max){
                    user.setUserData(UserDataEnum.MAX_WORLD_BOSS_HURT,attackValue,true);
                }
            }
        }
//        if(user.currentMap.type==MapTypeEnum.NEWGAME_FIGHT){
//            user.actState = UserActState.CAMP;
//            AllSql.userSql.saveFightData(user);
//
//                new GoMapCmd().execute(client, user, 1,true);
//
//            return;
//        }
          // if(rqst.money>user.fightingLoot.totalMoney)rqst.money=user.fightingLoot.totalMoney;
        boolean isSuccess = false;
        if((rqst.stars.size()>0 && ((List<Byte>)rqst.stars).get(0)>0)||mapAttackMode==true) {
            isSuccess = true;
        user.addExpInGaming(user.fightingLoot.totalExp);
        user.fightingLoot.totalExp = 0;
        ArrayList<PropPVo> loots = new ArrayList<>();
        int collectionID=0,collectionCount=0;
        user.traceBug("getLoot");

            if(rqst.useHpDrug>0&&user.costUserDataAndProp(UserDataEnum.HP_COUNT, rqst.useHpDrug, true,ReasonTypeEnum.GO_MAP)==false)return;
//int [] userdatas=new int[UserDataEnum.MAX];
            int money = 0;
         for (PropPVo pvo :user.fightingLoot.totalProps){
             if (PropModel.isMissionItem(pvo.baseID)) {
                 collectionID = pvo.baseID;
                 collectionCount++;
             } else {
//                 if( PropModel.isAttributeItem(pvo.baseID)){
//                     userdatas[pvo.baseID]+=pvo.count;
//                 }else if(PropModel.isSoul(pvo.baseID)){
////                     user.illustratedModel.addSoul(user.currentMap.ID,pvo);
//                 }else if(PropModel.isDragonEgg(pvo.baseID)){
//                     PropBaseVo propBaseVo = Model.PropBaseMap.get(pvo.baseID);
//                     user.cacheUserVo.dragonEggModel.addDragonEgg(propBaseVo);
//                 }else {
                 if(pvo.baseID == UserDataEnum.MONEY){
                     money += pvo.count;
                 }else{
                     loots.add(pvo);
                 }
//                  }
             }
         }
//            for(MonsterGroupPVo monsterGroupPVo : user.illustratedModel.monsterGroupList){
//                System.out.println("===========illustratedModel : mapIndex " + monsterGroupPVo.mapIndex + "===============");
//                for(MonsterPVo monsterPVo : monsterGroupPVo.monsterList){
//                    System.out.println("===========illustratedID " + monsterPVo.illustratedID +"   "+ monsterPVo.count+ "===============");
//                }
//            }

//            user.illustratedModel.saveSqlData();
        user.fightingLoot.totalProps=new ArrayList<>();
        if( user.fightingLoot.dynamicTotalProps.size()>0) {

            for (short tempID : rqst.dynamicProps) {
                ArrayList<PropPVo> list = user.fightingLoot.dynamicTotalProps.get(tempID);



                if (list == null) continue;

                user.fightingLoot.dynamicTotalProps.remove(tempID);
                for(PropPVo pvo : list) {
                    // if (PropModel.isMissionItem(pvo.baseID)) {
                    //   collectionID = pvo.baseID;
                    //  collectionCount++;
                    //} else {
//                   if( PropModel.isAttributeItem(pvo.baseID)){
//                       userdatas[pvo.baseID]+=pvo.count;
//                   }else {
                    if (pvo.baseID == UserDataEnum.MONEY) {
                        money += pvo.count;
                    } else {
                        loots.add(pvo);
                    }
//                    }
                    //  }
                }
            }
        }
            user.propModel.addListToBag(loots,ReasonTypeEnum.GO_MAP);
            if(money > 0){
                user.addUserData(UserDataEnum.MONEY,money,true,ReasonTypeEnum.GO_MAP);
            }
            new AwardShowRspd(client,loots);
//        for (byte i=0;i<UserDataEnum.MAX;i++ ){
//            if(userdatas[i]>0){
//                int rstVal= user.addUserData(i, userdatas[i], true);
//               // DCGameLog.getInstance().AddLog(GameLogType.COINGAIN, new String[]{user.guid + "", i + "", (userdatas[i])  + "", rstVal + "", "getLoot"});
//
//            }
//        }
            switch (user.currentMap.type){
                case MapTypeEnum.NORMAL:
                    if(user.currentMap.isBoss == 2){
                        addCarnetCount(user,user.currentMap.ID);
                    }
                    user.addUserData(UserDataEnum.LJ_NORMAL_PASS_COUNT,1,true);
                    if(mapAttackMode==false || MapAttackCmd.isCheck == 1){
                        user.costUserDataAndProp(UserDataEnum.TILIZHI, user.currentMap.energe, true,ReasonTypeEnum.GO_MAP);
                    }
                    break;
                case MapTypeEnum.YOU_MIN:
                    if(user.currentMap.ID%100<=2){
                        addCarnetCount(user,user.currentMap.ID);
                    }
                    user.addUserData(UserDataEnum.LJ_YOU_MIN_PASS_COUNT,1,true);break;
                case MapTypeEnum.YUAN_SHU:
                    if(user.currentMap.ID%100<=2){
                        addCarnetCount(user,user.currentMap.ID);
                    }
                    user.addUserData(UserDataEnum.LJ_YUAN_SU_PASS_COUNT,1,true);break;
                case MapTypeEnum.DRAGON:
                    if(user.currentMap.ID%100<=2){
                        addCarnetCount(user,user.currentMap.ID);
                    }
                    user.addUserData(UserDataEnum.LJ_DRAGON_PASS_COUNT,1,true);break;
            }
if(mapAttackMode==false) {
    if (user.currentMap.type == MapTypeEnum.NORMAL) {
        MapQualityPVo qualityVo = user.mapQualityMap.get(user.currentMap.ID);
        boolean needUpdateMapQuality = false;
        if (user.currentMap.stars == null || rqst.stars.size() > user.currentMap.stars.length / 3 + 1){
            ArrayList<Byte> list = new ArrayList<>();
            list.add((byte)1);
            rqst.stars = list;
        }

        if (qualityVo == null) {
            qualityVo = new MapQualityPVo();
            qualityVo.ID = user.currentMap.ID;
            qualityVo.stars = rqst.stars;
            user.mapQualityMap.put(qualityVo.ID, qualityVo);
            needUpdateMapQuality = true;
            user.checkFunctionOpen(false,false);
            user.addUserData(UserDataEnum.LJ_STARS_COUNT,rqst.stars.size(),true);
        } else {
            if (qualityVo.stars.size() < rqst.stars.size()) {
                user.addUserData(UserDataEnum.LJ_STARS_COUNT,rqst.stars.size()-qualityVo.stars.size(),true);
                qualityVo.stars = rqst.stars;
                needUpdateMapQuality = true;
            }
        }

        if (needUpdateMapQuality) {

                    user.calculateFbScore();


            AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPQUALITY, "'" + MapQualityPVoJoin.instance.joinCollection(user.mapQualityMap.values()) + "'");
            //排序之前不能改变分数否则原来的数组就次序被打乱
            int fbScore = user.chaperScore;

            if (user.cacheUserVo.rankFbVo == null) {
                user.cacheUserVo.rankFbVo = new RankUserFbVo(user.cacheUserVo);
                RankModel.rankFbList.addEnd(user.cacheUserVo.rankFbVo);
            }

            RankModel.rankFbList.sortItem(user.cacheUserVo.rankFbVo, fbScore);
            user.cacheUserVo.fbScore = fbScore;
            user.cacheUserVo.lastMap = user.currentMap.ID;

            //; us.mapQualityMap.put(mpvo.ID,mpvo);
        }
        new UpdateMapQualityRspd(client, qualityVo);
    }
}
            if (user.currentMap.type == MapTypeEnum.ELITE) {
                EliteModel eliteModel = user.eliteModel;
                if(!eliteModel.elitePassedMap.contains((short)user.currentMap.ID)){
                    eliteModel.elitePassedMap.add((short)user.currentMap.ID);
                    eliteModel.saveSqlData();
                }
            }

            if(user.currentMap.type == MapTypeEnum.REDNESS){
                RP_RoomVo roomVo = user.rednessModel.myRoom;
                if(roomVo != null)roomVo.isInGame = 0;
            }

            if(user.currentMap.type == MapTypeEnum.GANG){
                MapModel.success(user,user.currentMap);
            }

            if (user.currentMap.type == MapTypeEnum.TWOER) {
                user.wormNestModel.challengeSuccess(client);

            }
//            new AddPropListRspd(client, PropCellEnum.BAG, loots);
            if(collectionID!=0) {
                //  user.missionModel.progressBuyCollect(collectionID,collectionCount);

            }
            else if(rqst.stars.size()>0) {
                //user.missionModel.progressBuyKill(user.currentMap);
                user.missionModel.progressBuyAct(MissionConditionEnum.SURVEY,user.currentMap.ID);

            }
            user.missionModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON,user.currentMap.type);
            user.activationModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON,user.currentMap.type);
}else if(rqst.stars.size()>0 && (((List<Byte>)rqst.stars).get(0)==-1 || ((List<Byte>)rqst.stars).get(0)==-2)){
    //die

            if(user.currentMap.type == MapTypeEnum.GANG){
                MapModel.fail(user,user.currentMap,rqst.bossBlood);
            }

            if(user.currentMap.type == MapTypeEnum.WORLDBOSS || user.currentMap.type == MapTypeEnum.REDNESS || user.currentMap.type == MapTypeEnum.GANG){
                user.activationModel.progressBuyAct(MissionConditionEnum.COMPLETE_DUNGEON,user.currentMap.type);
            }

            user.fightingLoot.totalExp = 0;
            user.fightingLoot.totalProps=new ArrayList<>();
}

        if(user.currentMap.type == MapTypeEnum.REDNESS){
            RP_RoomVo roomVo = user.rednessModel.myRoom;
            if(roomVo != null){
                if(isSuccess){
                    if (roomVo.guest != null && roomVo.ownner == user.cacheUserVo && roomVo.getTeamer(user.cacheUserVo).isRobot()) {
                        roomVo.needRobotTime = JkTools.getGameServerTime(client)+ RednessModel.NEED_ROBOT_READY;
                    }
                }else{
                    if (roomVo.guest == null || roomVo.getTeamer(user.cacheUserVo).isRobot()) {
                        roomVo.isInGame = 0;
                    }
                    RP_RoomExitRqst rp_roomExitRqst = new RP_RoomExitRqst();
                    rp_roomExitRqst.isMe = true;
                    new RP_RoomExitCmd().execute(client,user,rp_roomExitRqst);
                }
            }
        }



            // if(user.currentMap.isBoss==1){
         //   user.activationModel.progressBuyAct(MissionActEnum.KILL_COMMON_BOSS);

      //  }
        if (user.currentMap.type == MapTypeEnum.TWOER) {
            if(rqst.stars.size()>0 && ((List<Byte>)rqst.stars).get(0)==100){
                GoTowerRqst goTowerRqst=new GoTowerRqst();
                goTowerRqst.towerID=1;
                new GoTowerCmd().execute(client,user,goTowerRqst);
                return;
            }
        }

       if( mapAttackMode==false) {
           user.propModel.trampShop = null;
          // user.actState = UserActState.CAMP;
          // AllSql.userSql.saveFightData(user);
           if (needJumpMap) {
               MapBaseVo mapBaseVo = user.currentMap;
               short keyMapID=(short)mapBaseVo.ID;
               if(mapBaseVo.countLimit<0){
                   keyMapID=(short)-mapBaseVo.countLimit;
               }
               if(mapBaseVo.countLimit != 0 && user.mapEnteredMap.containsKey(keyMapID)){
                   MapEnteredPVo mapEnteredPVo = user.mapEnteredMap.get(keyMapID);
                   if(JkTools.indexOf(Model.GameSetBaseMap.get(66).intArray,user.currentMap.type) >= 0){
                       if(isSuccess){
                           mapEnteredPVo.value++;
                           if (mapBaseVo.coolingTime != 0) {
                               mapEnteredPVo.time = JkTools.getGameServerTime(client) + mapBaseVo.coolingTime;
                           }
                       }
                   }else{
                       if (mapBaseVo.coolingTime != 0) {
                           mapEnteredPVo.time = JkTools.getGameServerTime(client) + mapBaseVo.coolingTime;
                       }
                   }

                   ArrayList<MapEnteredPVo> list = new ArrayList<>();
                   list.add(mapEnteredPVo);
                   new MapEnterTimesRspd(client,list);
                   AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
               }
               new GoMapCmd().execute(client, user, 1, true);
           }
       }else{
           MapBaseVo mapBaseVo = Model.MapBaseMap.get(1);
           if (mapBaseVo == null) {
               return;
           }
           user.currentMap = mapBaseVo;
       }
    }

    private void addCarnetCount(User user,int mapID){
        if(user.carnetCountMap.containsKey(mapID)){
            IntIntPVo intIntPVo = user.carnetCountMap.get(mapID);
            intIntPVo.value++;
        }else{
            IntIntPVo intIntPVo = new IntIntPVo();
            intIntPVo.key = mapID;
            intIntPVo.value = 1;
            user.carnetCountMap.put(mapID,intIntPVo);
        }
        AllSql.userSql.update(user,AllSql.userSql.FIELD_CARNET_COUNT,"'"+ CarnetCountPVoJoin.instance.joinCollection(user.carnetCountMap.values()) +"'");
        new UpdateCarnetCountRspd(user.client,user.carnetCountMap.values());
    }
}
