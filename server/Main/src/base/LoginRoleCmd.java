package base;

import airing.AiringModel;
import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.comm.ThreadCallBack;
import gluffy.utils.JkTools;
import gluffy.utils.LogManager;
import mission.LimitTimeModel;
import mission.MissionModel;
import protocol.*;
import rank.RankModel;
import rank.RankUserZdlVo;
import redness.RP_RoomExitCmd;
import snatch.SnatchModel;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;
import java.util.Random;


public class LoginRoleCmd extends BaseRqstCmd {
    public boolean autoLoadMode=false;
    public LoginRoleCmd() {
        super();
    }
    public LoginRoleCmd(boolean autoLoadMode) {
        super();
        this.autoLoadMode=autoLoadMode;
    }
    @Override
    public void execute(Client client,User user, BaseRqst baseRqst) {
        ThreadCallBack threadCallBack = new ThreadCallBack(aVoid -> {
            try{
                executeOnCallback(client,user,baseRqst);
            }catch (Exception e){
                new ServerTipRspd(client,(short)(992),"992");
                LogManager.printError(e);
            }

        });
        threadCallBack.submitAndShutdown();
    }

	public void executeOnCallback(Client client,User user, BaseRqst baseRqst) {

//        try {
//            if(client.guid%2==0) {
//                Thread.sleep(5000);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        byte selID =0;
 if(baseRqst!=null) {
       selID = ((LoginRoleRqst) baseRqst).baseID;
     client.passportVo.lastRoleID=selID;
     AllSql.passportSql.update(client.passportVo,AllSql.passportSql.FIELD_LAST_ROLE_ID,client.passportVo.lastRoleID);
  }else {
     selID=    client.passportVo.lastRoleID;
 }
        CacheUserVo cu = client.passportVo.userMap.get(selID);
        if(cu==null)return;
        AllSql.userSql.loadone(client,cu );


        user=client.currentUser;

//        if(autoLoadMode==false&&user==null){
//            //client.currentUser=user=createWhenVoid(client,selID);
//            return;
//        }
        if(user==null)return;
        if(user.cacheUserVo.status != 0){
            new ServerTipRspd(client,(short)996,null);
            return;
        }
        client.userMap.put(user.baseID,user);
		user.initGame();

	if(user.getUserData(UserDataEnum.LEVEL)==0){
		 PropPVo propPVo=new PropPVo();
 			propPVo.baseID=user.baseVo.defaultWeapon;
        propPVo.count = 1;
        AllSql.propSql.insertNew(user, propPVo, PropCellEnum.EQUIP);
        user.propModel.setEquipByIndex((byte) Model.PropBaseMap.get(propPVo.baseID).type, propPVo);

//        if(Model.devCode == 1){
//            if(user.baseVo.giftId != 0){
//                PropPVo testVo=new PropPVo();
//                testVo.baseID=user.baseVo.giftId;
//                user.propModel.addInBag(testVo,true);
//            }
//        }

//  user.setUserData(UserDataEnum.LAST_DAY, 999,true);
//        user.setUserData(UserDataEnum.LAST_RUN_DAY, 999,true);
   //     user.setUserData(UserDataEnum.TILIZHI,200);
        user.setUserData(UserDataEnum.LEVEL,1,true);
//        if(user.cacheUserVo.rankArenaVo==null){
//            user.cacheUserVo.rankArenaVo=new RankUserArenaVo(user.cacheUserVo);
//            RankModel.rankArenaList.addEnd(user.cacheUserVo.rankArenaVo);
//        }
//        new ResetDailyDataCmd().reset(client,user,true,false);
//        new ResetRundayDataCmd().reset(client,user,true,false);
        user.userUpdateModel.init();
        for(LimitTimeActivationBaseVo vo : LimitTimeModel.creartList){
            MissionModel missionModel = new MissionModel(user,(byte)vo.ID);
            user.limitTimeMap.put((byte)vo.ID,missionModel);
            missionModel.startTime = JkTools.getRundayTime(client);
            missionModel.saveSqlData();
        }
      // user.setUserData(UserDataEnum.SKIN, user.baseVo.defaultSkin);
        //user.setUserData(UserDataEnum.MONEY, 3500);
//        SkillPVo vo=new SkillPVo();
//        vo.baseID=41;
//        vo.level= (byte) 1;
//        user.cacheUserVo.skillModel.setItemByIndex((byte)0,vo);
      //  DCGameLog.getInstance().AddLog(GameLogType.REGISTER, new String[]{user.guid + ""});
//        for(MailPVo mailPVo : LoopSendMailModel.initMailList){
//            if(mailPVo.deadDay == -1 && client.passportVo.isOldUser != 1) continue;  //老用户才发的邮件
//            user.mailModel.addMail(mailPVo,false);
//        }
//        user.mailModel.flag = true;
    }else if(user.sqlInited==false){

		AllSql.propSql.loadone(user);
		user.sqlInited=true;
	}
        client.roleLogined=true;
for (PropPVo pVo:user.propModel.bagItems.values()){
    SnatchModel.addOwnner(pVo.baseID, user.cacheUserVo);

}
//        new ResetDailyDataCmd().reset(client,user,false,false);
//        new ResetRundayDataCmd().reset(client,user,false,false);
        user.userUpdateModel.checkUpdate();
      //  HireModel.onLoadUser(user);
        //DynamicUserGroup.instance.onEnter(user);
        //user.cacheUserVo.skillModel.talents.clear();// for test
        //user.cacheUserVo.skillModel.skills.clear();// for test
//        user.flushDataTime(UserDataEnum.SNATCH_FIGHT_COUNT,UserDataEnum.NEXTTIME_SNATCH,true);
        user.flushDataTime(UserDataEnum.TILIZHI,UserDataEnum.NEXTTIME_Tili,true);
        user.setUserData(UserDataEnum.LAST_LOGIN_TIME, JkTools.getGameServerTime(client), true);
        if(JkTools.getGameServerTime(client) - user.getUserData(UserDataEnum.WormNestNextEndTime) >= 0 && user.getUserData(UserDataEnum.WormNestNextEndTime) != 0 && user.getUserData(UserDataEnum.WormNestNextEndTime) != -1){
            user.setUserData(UserDataEnum.WormNestNextEndTime,-1,true);
        }
        if(autoLoadMode==false) {
            long gangId = -1;
            String gangName = "尚未加入公会";
            if(user.cacheUserVo.gang.gangVo != null){
                gangId = user.cacheUserVo.gang.gangVo.gangID;
                gangName = user.cacheUserVo.gang.gangVo.gangName;
            }
            ArrayList<Byte> vipAwardList = new ArrayList<>();
            if(client.passportVo.getVipAward != null && !"".equals(client.passportVo.getVipAward)){
                String[] strArr = client.passportVo.getVipAward.split(",");
                for(String str : strArr){
                    vipAwardList.add(Byte.parseByte(str));
                }
            }
            ArrayList<Byte> vipGiftList = new ArrayList<>();
            if(client.passportVo.getVipAward != null && !"".equals(user.buyVipGift)){
                String[] strArr = user.buyVipGift.split(",");
                for(String str : strArr){
                    vipGiftList.add(Byte.parseByte(str));
                }
            }
            new InitGameRoleRspd(client, user.guid, user.baseID,user.cacheUserVo.portrait, JkTools.intArrayAsList(user.userdata), user.propModel.bagItems.values(), JkTools.asListTrimNull(user.cacheUserVo.equipItems),
                    user.cacheUserVo.skillModel.skills.values(), user.mapQualityMap.values(),user.missionModel.completedList,user.cacheUserVo.skillModel.talents.values(),user.activationModel.completedList,user.achieveModel.completedList,user.mapEnteredMap.values(),user.cacheUserVo.skin
            ,gangId,gangName,vipAwardList,vipGiftList, user.cacheUserVo.dragonEggModel.getEggPVoList(user.cacheUserVo),user.getTili,user.cacheUserVo.yunwaID);
           user.fightingLoot = null;
            ChangeMainCmd.flag = false;
           int goMapId=1;
//           if( user.getUserData(UserDataEnum.LEVEL)<=1){
//                goMapId=996;
//           }
//            if(user.guide == null || "".equals(user.guide))goMapId = 2;
           new GoMapCmd().execute(client, user, goMapId, true);

        }else {
           if (user.fightingLoot == null || user.fightingLoot.mapID == 1) {
               new GoMapCmd().execute(client, user, 1, false);

           } else {
               new GoMapCmd().executeAuto(client, user, user.fightingLoot.mapID, false);
           }
       }
if(user.cacheUserVo.rankVo==null){
    user.cacheUserVo.rankVo =new RankUserZdlVo(user.cacheUserVo);
    RankModel.rankZdlList.addEnd(user.cacheUserVo.rankVo);
}

//        int oldZdl=user.zdl;
//        user.zdl = user.calculateZDL();
//        if(oldZdl!=user.zdl) {
//
//            user.cacheUserVo.zdl=user.zdl;
//            if(user.cacheUserVo.gang.gangVo != null){
//                user.cacheUserVo.gang.gangVo.zdl +=user.zdl-oldZdl;
//            }
//            AllSql.userSql.update(user, AllSql.userSql.FIELD_ZDL, user.zdl);
//            RankModel.rankZdlList.sortItem(user.cacheUserVo.rankVo,user.zdl);
//        }

        for (MissionBaseVo missionBaseVo : user.missionModel.acceptedList.keySet()){
            new MissionProcessRspd(client,missionBaseVo.ID,user.missionModel.acceptedList.get(missionBaseVo).shortValue());
        }
        for (MissionBaseVo missionBaseVo : user.activationModel.acceptedList.keySet()){
            new MissionProcessRspd(client,missionBaseVo.ID,user.activationModel.acceptedList.get(missionBaseVo).shortValue());
        }
        if(user.limitTimeMap.size() > 0){
            MissionModel.sendLimitTimeInfo(user);
        }
        new ChannelInfoRspd(client,user.channelStr);
        new RechargeShopInfoRspd(client,user.rechargeModel.rechageSet);
        user.updateZDL();
        user.checkFunctionOpen(true,false);


        user.cacheUserVo.attributeModel.saveSqlData();

        AiringModel.flushAiringList();
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        for(AiringMessagePVo pVo : AiringModel.sysAiringList){
            list.add(pVo);
            if(list.size() > 5)break;
        }
        new AiringListRspd(client,list);
        new GuideRspd(client,user.guide);
        new UpdateEquipQualityRspd(client,user.equipQualityMap.values());
        new UpdateCarnetCountRspd(client,user.carnetCountMap.values());
        new UpdateStoneQualityRspd(client,user.stoneCountMap.values());
        new UpdateHarvestEggQualityRspd(client,user.eggQualityMap.values());
        new DragonStoneOwnLevelRspd(client,user.dragonStoneOwnLevel);
        new FreeLotteryDragonRspd(client,user.dragonModel.moneyLottery,user.dragonModel.diamondLottery);
        new DragonStoneInitGetRspd(client,user.cacheUserVo.equipDragonStone.values());
      //  new WB_BuffRspd(client,user.worldBossModel.buffSet);
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo != null){
            airing(client);
        }

//        if(user.missionModel.hasAcceptedMission(MissionTypeEnum.DAILY)==false){
//            user.missionModel.flushDaily();
//        }
        //因为还没存盘 所以需要每次登陆计算 以后不需要
//user.missionModel.calculateNewMission();
//user.activationModel.calculateNewMission();


        user.calculateFbScore();

//user.activationModel.addNewActiveMission();
      //  if(user.dragonModel.mainDragon!=null){
           // new DragonSelectRspd(client,user.dragonModel.getMainDragonID())
       // }

//        Model.campUsers.put(user.guid,user.cacheUserVo);
//        int campUserSize=Model.campUsers.size();
//        if(campUserSize>=8){
//           Long guid= (Long) Model.campUsers.keySet().toArray()[new Random().nextInt(campUserSize)];
//            Model.campUsers.remove(guid);
//        }
        RP_RoomExitRqst rqst = new RP_RoomExitRqst();
        rqst.isMe = true;
        new RP_RoomExitCmd().execute(client,user,rqst);
    }


    private static void airing(Client client){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(14);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int index = client.passportVo.vip;
        if(index%airingVo.divisor != 0)return;
        if(JkTools.compare(index,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&&JkTools.compare(index,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        pVo.msg = airingVo.msg.replace("{1}",String.valueOf(index)).replace("{2}",client.currentUser.cacheUserVo.name);
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
