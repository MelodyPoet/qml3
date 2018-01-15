package talk;

import airing.AiringModel;
import arena.ArenaModel;
import base.*;
import comm.*;
import dragon.DragonCacheModel;
import dragon.DragonEggModel;
import dragon.DragonEggVo;
import dragon.DragonSelectCmd;
import emperor.EmperorAwakenCmd;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.AbsClient;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import gluffy.utils.RankSortedList;
import mail.LoopSendMailModel;
import mail.MailModel;
import mission.MissionCompleteCmd;
import prop.UseBagPropCmd;
import protocol.*;
import redness.RednessLoopModel;
import redness.RednessModel;
import sendMsg.NTestModel;
import sendMsg.SendMsgModel;
import sqlCmd.AllSql;
import table.*;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class TalkCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
//        TalkModel.talkModel();
        TalkRqst rqst = (TalkRqst) baseRqst;
        if (rqst.msg.startsWith("/") == false) {
            if(user.cacheUserVo.passportVo==null)return;
            if(rqst.classType == TalkEnum.COMMON){
                if(rqst.type!=TalkEnum.TEXT&&rqst.type!=TalkEnum.VOICE&&rqst.type!=TalkEnum.TEAM&&rqst.type!=TalkEnum.REDPACKET)return;
                TalkModel.addTalkMsgInTimeList(user, rqst.msg,rqst.classType,rqst.type,rqst.redpacketsID);
                user.activationModel.progressBuyAct(MissionConditionEnum.WORLD_TREE_MSG,0);
            }

            if(rqst.classType == TalkEnum.GANG){
                if(rqst.type!=TalkEnum.TEXT)return;
                TalkModel.addTalkMsgInTimeList(user, rqst.msg,rqst.classType,rqst.type,rqst.redpacketsID);
            }
            GetTalkBackRqst getTalkBackRqst=new GetTalkBackRqst();
            getTalkBackRqst.page=0;
            getTalkBackRqst.type=-1;
       new     GetTalkBackCmd().getTalkBack(client,user,getTalkBackRqst,true);
//            }else if(rqst.roleID==-1){
//                //炫耀装备
//                PropPVo pvo=null;
//                if(rqst.msg.startsWith(PropCellEnum.EQUIP+"")){
//
//                }else {
//                    pvo=user.propModel.getPropInBag(Long.parseLong(rqst.msg.substring(2)));
//                }
//                if(pvo==null)return;
//                String msg=pvo.baseID+"_"+pvo.enchant+"_"+pvo.intensify;
//                TalkModel.addTalkMsg(user.roomID,user.guid, user.name, msg,true);
//
//            }else{
//                TalkModel.addTalkMsg(rqst.roleID,user.guid, user.name, rqst.msg);
//            }
         //   new GetTalkBackCmd().execute(client, user, null);

        } else{
            String[] msgs = rqst.msg.split(" ");
            switch (msgs[0]) {
                case "/item":
                {
                    int count = 1;
                    int baseID = Integer.parseInt(msgs[1]);
                    if (msgs.length > 2) {
                        count = Integer.parseInt(msgs[2]);
                    }
                    ArrayList<PropPVo> list = new ArrayList<>();
                    if (Model.PropBaseMap.get(baseID).maxCount > 1) {
                        PropPVo propPVo = new PropPVo();
                        propPVo.baseID = baseID;
                        propPVo.count = count;
                        list.add(propPVo);
                    } else {
                        for (int i = 0; i < count; i++) {

                            PropPVo propPVo = new PropPVo();
                            propPVo.baseID = baseID;
                            propPVo.count = 1;
                            if (Model.PropBaseMap.containsKey(propPVo.baseID) == false) return;
                            list.add(propPVo);
                        }
                    }
                    user.propModel.addListToBag(list,ReasonTypeEnum.GM);
                }
                break;
                case "/delitem":
                {
                    int count = 1;
                    int baseID = Integer.parseInt(msgs[1]);
                    if (msgs.length > 2) {
                        count = Integer.parseInt(msgs[2]);
                    }
                    user.costUserDataAndProp(baseID, count,true,ReasonTypeEnum.GM);

                }
                break;
                case "/temp":
                {
         MissionCompleteRqst rqst1=new MissionCompleteRqst();
                    rqst1.msID=Integer.parseInt(msgs[1]);
                    new MissionCompleteCmd().execute(client,user,rqst1);
                }

                    break;


                case "/udata":
                    user.setUserData( Byte.parseByte(msgs[1]), Integer.parseInt(msgs[2]), true,ReasonTypeEnum.GM);

                    break;
                case "/addudata":
                    byte userDataEnum = Byte.parseByte(msgs[1]);
                    int num = Integer.parseInt(msgs[2]);
                    if(userDataEnum == UserDataEnum.LEVEL){//等级上限
                        int level = user.getUserData(UserDataEnum.LEVEL);
                        if(level >=Model.userLevelMax){
                            break;
                        }else if((level+num)>=Model.userLevelMax){
                            user.addUserData( userDataEnum, Model.userLevelMax, true);
                            break;
                        }
                    }
                    user.addUserData( userDataEnum, num, true);


                    break;
                case "/go":
                    new GoMapCmd().execute(client, user, Integer.parseInt(msgs[1]), true);

                    break;
                case "/openmap":
                    int openMapID = Integer.parseInt(msgs[1]);
                    HashMap<Integer, MapBaseVo> allNormalMap = new HashMap<>();
                    for (MapBaseVo mapVo : Model.MapBaseMap.values()) {
                        if(mapVo.type != MapTypeEnum.NORMAL)
                            continue;
                        if(mapVo.ID <= openMapID)
                        {
                            MapQualityPVo qualityVo = new MapQualityPVo();
                            qualityVo.stars = new ArrayList<>();
                            qualityVo.ID = mapVo.ID;
                            qualityVo.stars.add((byte)1);
                            user.mapQualityMap.put(qualityVo.ID, qualityVo);
                            new UpdateMapQualityRspd(client, qualityVo);
                        }
                    }
                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPQUALITY, "'" + MapQualityPVoJoin.instance.joinCollection(user.mapQualityMap.values()) + "'");
                    break;
                case "/fight": {
                    int count = 1;
                    if (msgs.length == 3) {
                        count = Integer.parseInt(msgs[2]);
                    }
                    if (count > 10) return;

                    for (int i = 0; i < count; i++) {


                        new GoMapCmd().execute(client, user, Integer.parseInt(msgs[1]), false);
                      //  if (user.fightingLoot.totalExp == 0) return;
                        GetLootRqst lgRqst = new GetLootRqst();
                         lgRqst.stars.add((byte)1);


                        new GetLootCmd().execute(client, user, lgRqst, false);
                    }
                }
                break;
                case "/tower": {



                }
                break;


                case "/ms": {
                   int msID= Integer.parseInt(msgs[1]);
                    MissionBaseVo msvo=Model.MissionBaseMap.get(msID);
                    if(msvo.showLevel >0){
                        user.setUserData(UserDataEnum.LEVEL, msvo.showLevel, true);
//                        user.setUserData(UserDataEnum.MONEY, 1000000, true);
//                        user.setUserData(UserDataEnum.DIAMOND, 10000, true);x`

                    }
                    user.missionModel.acceptedList.clear();
                    user.missionModel.completedList.clear();
                    while (true){
                        if(msvo==null||msvo.needMission==0)break;
                        if(user.missionModel.completedList.contains(msvo.needMission))break;
                        user.missionModel.completedList.add(msvo.needMission);
                        new MissionCompleteRspd(client,msvo.needMission);

                        msvo=Model.MissionBaseMap.get(msvo.needMission);
                    }

                    user.missionModel.accept(msID);
                  user.missionModel.saveSqlData();

                }
                break;
                case "/dragon":
                {
                    DragonCacheModel dragonCacheModel = user.cacheUserVo.dragonCacheModel;
                    short id=Short.parseShort(msgs[1]);
                    int count=Integer.parseInt(msgs[2]);
                    DragonPVo dragonPvo;
                    if(dragonCacheModel.dragonsMap.containsKey(id)){
                        dragonPvo = dragonCacheModel.dragonsMap.get(id);
                        dragonPvo.count += count;
                    }else{
                        dragonPvo =new DragonPVo();
                        dragonPvo.baseID=id;
                        dragonPvo.level=0;
                        dragonPvo.count=count;
                        dragonCacheModel.dragonsMap.put(dragonPvo.baseID,dragonPvo);
                    }
                    new AddDragonRspd(user.client,dragonPvo);
                    dragonCacheModel.saveSqlData();

                }
                    break;
                case "/clearwt":
                {
                    TalkModel.rankLikeList=new RankSortedList<>();
                    TalkModel.rankLikeMap.clear();
                    TalkModel.talkMsgMap=new HashMap<>();
                    TalkModel.talkMsgList=new ArrayList<>();
                    TalkModel.redPacketMap=new HashMap<>();
                    TalkModel.userLikesMap=new HashMap<>();
                    TalkModel.systemMsgNum = 0;
                    TalkModel.redPackMsgNum = 0;
                    TalkModel.bossMsgNum = 0;
                }
                break;
                case "/reset":
                {
                    user.setUserData(UserDataEnum.WormNestResetTime,1,true);
                }
                break;

                case "/mail":
                {
                    int[] prop = JkTools.readArray(msgs[2]);
//                    int money = Integer.parseInt(msgs[2]);
//                    int diamond = Integer.parseInt(msgs[3]);
                    int cycleType = Integer.parseInt(msgs[6]);
                    int day = Integer.parseInt(msgs[7]);
                    int hours = Integer.parseInt(msgs[8]);
                    MailBaseVo vo = new MailBaseVo();
                    vo.ID = Integer.parseInt(msgs[1]);
                    vo.senderName = msgs[3];
                    vo.title = msgs[4];
                    vo.msg = msgs[5].replace('_',' ');
                    vo.prop = prop;
//                    vo.money = money;
//                    vo.diamond = diamond;
                    vo.cycleType = cycleType;
                    vo.day = day;
                    vo.hours = hours;
                    Model.MailBaseMap.put(vo.ID,vo);
                    LoopSendMailModel.classify(vo);
                }
                break;
                case "/mymail":
                {
                    int id= Integer.parseInt(msgs[1]);
                    user.mailModel.sendMail(id);
                }
                case "/hismail":
                {

                    int[] prop = JkTools.readArray(msgs[3]);
                    byte baseID = Byte.parseByte(msgs[2]);
                    //要传guid 先注释了
                    Client his = null;//Client.getOne(msgs[1],Model.serverID);
                    if(his == null)return;
                    User hisUser = his.userMap.get(baseID);
                    if(hisUser == null)return;
                    MailPVo mailPVo = new MailPVo();
                    mailPVo.senderID = 0;
                    mailPVo.senderName = "露娜";
                    mailPVo.receiverID = hisUser.guid;
                    mailPVo.receiverTime = JkTools.getGameServerTime(null);
                    mailPVo.isRead = 0;
                    mailPVo.title = "系统邮件";
                    mailPVo.msg = msgs[4].replace('_',' ');
                    mailPVo.prop = MailModel.getAnnexPropList(prop);
//            mailPVo.money = mailBaseVo.money;
//            mailPVo.diamond = mailBaseVo.diamond;
                    Calendar calendar = JkTools.getCalendar();
                    int day = calendar.get(Calendar.DAY_OF_YEAR);
                    int year = calendar.get(Calendar.YEAR);
                    mailPVo.deadDay = day + JkTools.getBaseDay(year) + MailModel.LIMIT_DAYS;
                    hisUser.mailModel.sendMail(mailPVo,true);
                }
                break;
                case "/talk":
                {
                    int type = Integer.parseInt(msgs[1]);
                    int redpacketsID = Integer.parseInt(msgs[2]);
                    int cycleType = Integer.parseInt(msgs[5]);
                    int day = Integer.parseInt(msgs[6]);
                    int hours = Integer.parseInt(msgs[7]);
                    int minutes = Integer.parseInt(msgs[8]);
                    if(type!=TalkEnum.TEXT&&type!=TalkEnum.VOICE&&type!=TalkEnum.BOSS&&type!=TalkEnum.REDPACKET&&type!=TalkEnum.SYSTEM)return;
                    WorldTreeBaseVo vo = new WorldTreeBaseVo();
                    vo.ID = ++LoopSendTalkModel.baseID;
                    vo.senderName = msgs[3];
                    vo.classType = TalkEnum.OFFICIAL;
                    vo.type = type;
                    vo.msg = msgs[4];
                    vo.redpacketID = redpacketsID;
                    vo.cycleType = cycleType;
                    vo.day = day;
                    vo.hours = hours;
                    vo.minutes = minutes;
                    Model.WorldTreeBaseMap.put(vo.ID,vo);
                    LoopSendTalkModel.classify(vo);
                }
                break;
                case "/dragonstone":
                {
                    user.setUserData(UserDataEnum.DRAGON_STONE_BUY_COUNT,20,true);
                }
                break;
                case "/arena":
                {
                    user.setUserData(UserDataEnum.ARENA_FIGHT_COUNT,5,true);
                }
                break;
                case "/airing":
                {
                    AiringMessagePVo pVo = new AiringMessagePVo();
                    pVo.type = 0;
                    pVo.msg = msgs[3];
                    pVo.time = Byte.parseByte(msgs[1]);
                    pVo.space = Integer.parseInt(msgs[2]);
                    if(pVo.time > 0){
                        pVo.deadTime = JkTools.getGameServerTime(client) + pVo.space * 60 * (pVo.time - 1);
                    }else if(pVo.time == -1){
                        pVo.deadTime = JkTools.getRundayTime(null)+Model.ONE_DAY_TIME;
                    }else{
                        return;
                    }
                    AiringModel.sysAiringList.add(pVo);
                    ArrayList<AiringMessagePVo> list = new ArrayList<>();
                    list.add(pVo);
                    AiringModel.broadcast(list);
                }
                break;
                case "/getDay":
                {
                    AllSql.userSql.update(user,AllSql.userSql.FIELD_IS_GET_CLOSE_TEST,0);
                    user.isGetCloseTest = 0;
                    new ClosedTestTimeRspd(client,(byte)0,user.hadGetTime);
                }
                break;
                    case "/sendmessage":
                {
                    SendMsgModel.sendMessage(client,msgs[1],msgs[2],msgs[3],msgs[4],msgs[5]);
                }
                break;
//                case "/sendToGate":
//                {
//                    int group = Integer.parseInt(msgs[1]);
//                    int id = Integer.parseInt(msgs[2]);
//                    MessageBaseVo msgVo = Model.MessageBaseMap.get(id);
//                    if(msgVo == null)return;
//                    ArrayList<TelphoneBaseVo> telList = Model.TelphoneBaseMap.get(group);
//                    if(telList == null)return;
//                    StringBuffer sb = new StringBuffer();
//                    int i = 0;
//                    for(TelphoneBaseVo vo : telList){
//                        i++;
//                        if(i%200==0){
////                            System.out.println(i+sb.toString());
//                            SendMsgModel.sendMessage(client,sb.toString(),msgVo.id,msgVo.parameter1,msgVo.parameter2,msgVo.parameter3);
//                            sb = new StringBuffer();
//                        }
//                        sb.append(vo.telphone + ",");
//                    }
//                    sb.deleteCharAt(sb.length()-1);
////                    System.out.println(i+sb.toString());
//                    SendMsgModel.sendMessage(client,sb.toString(),msgVo.id,msgVo.parameter1,msgVo.parameter2,msgVo.parameter3);
//                }
//                break;
                case "/sendToGate":
                {
                    int group = Integer.parseInt(msgs[1]);
                    int id = Integer.parseInt(msgs[2]);
                    MessageBaseVo msgVo = Model.MessageBaseMap.get(id);
                    if(msgVo == null)return;
                    ArrayList<TelphoneBaseVo> telList = Model.TelphoneBaseMap.get(group);
                    if(telList == null)return;
                    for(TelphoneBaseVo vo : telList){
                            SendMsgModel.sendMessage(client,vo.telphone,msgVo.id,msgVo.parameter1,msgVo.parameter2,msgVo.parameter3);
                        }
                }
                break;
                case "/sendmodelid":
                {
                    int group = Integer.parseInt(msgs[1]);
                    ArrayList<TelphoneBaseVo> telList = Model.TelphoneBaseMap.get(group);
                    if(telList == null)return;
                    StringBuffer sb = new StringBuffer();
                    int i = 0;
                    for(TelphoneBaseVo vo : telList){
                        i++;
                        if(i%200==0){
//                            System.out.println(i+sb.toString());
                            SendMsgModel.sendMessage(client,sb.toString(),msgs[2],msgs[3],msgs[4],msgs[5]);
                            sb = new StringBuffer();
                        }
                        sb.append(vo.telphone + ",");
                    }
                    sb.deleteCharAt(sb.length()-1);
//                    System.out.println(i+sb.toString());
                    SendMsgModel.sendMessage(client,sb.toString(),msgs[2],msgs[3],msgs[4],msgs[5]);
                }
                break;
                case "/sendinvite":
                {
                    for(ArrayList<TelphoneBaseVo> telList : Model.TelphoneBaseMap.values()){
                        for(TelphoneBaseVo vo : telList){
                            String inviteCode = NTestModel.generateShortUuid();
                            SendMsgModel.sendInvite(client,vo.telphone,inviteCode);
                        }
                    }
                }
                break;
                case "/gangmoney":
                {
                    int count = Integer.parseInt(msgs[1]);
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    if(gangVo == null)return;
                    if(!gangVo.users.containsKey(user.guid))return;
                    GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
                    if(vo == null)return;
                    vo.setGangUserData(GangUserDataEnum.CONTRIBUTION,count,true);
                }
                break;
                case "/addparameter":
                {
                    int id = Integer.parseInt(msgs[1]);
                    MessageBaseVo vo = Model.MessageBaseMap.get(id);
                    vo.parameter1 = msgs[2];
                    vo.parameter2 = msgs[3];
                    vo.parameter3 = msgs[4];
                }
                break;
                case "/addmessage":
                {
                    int id = Integer.parseInt(msgs[1]);
                    MessageBaseVo vo = new MessageBaseVo();
                    vo.ID = id;
                    vo.id = msgs[2];
                    vo.parameter1 = msgs[3];
                    vo.parameter2 = msgs[4];
                    vo.parameter3 = msgs[5];
                    Model.MessageBaseMap.put(id,vo);
                }
                break;
                case "/buildmoney":
                {
                    int count = Integer.parseInt(msgs[1]);
                    user.cacheUserVo.gang.gangVo.exp += count;
                    AllSql.gangSql.update(user.cacheUserVo.gang.gangVo,AllSql.gangSql.FIELD_EXP,user.cacheUserVo.gang.gangVo.exp);
                }
                break;
                case "/ganglike":
                {
                    int count = Integer.parseInt(msgs[1]);
                    if(user.cacheUserVo.gang.gangVo == null)return;
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    if(!gangVo.users.containsKey(user.guid))return;
                    GangUserVo userVo = gangVo.users.get(user.guid);
                    userVo.likeTime += count;
                    AllSql.gangMemberSql.update(userVo,AllSql.gangMemberSql.FIELD_LIKE_TIME,userVo.likeTime);
                    gangVo.createGangInfoRspd(client);
                }
                break;
                case "/recharge":
                {
                    int count = Integer.parseInt(msgs[1]);
                    CachePassportVo vo = client.passportVo;
                    VipBaseVo oldVip = Model.VipBaseMap.get((int)vo.vip);
                    if(oldVip == null)return;
                    vo.rmb += count;
                    AllSql.passportSql.update(vo,AllSql.passportSql.FIELD_RMB,vo.rmb);
                    AutoBuyBaseVo autoBuyBaseVo = Model.AutoBuyBaseMap.get(count);
                    int diamond;
                    if(autoBuyBaseVo == null){
                     diamond = count;
                    }else{
                        diamond = autoBuyBaseVo.diamond;
                    }
                    vo.diamond += diamond;
                    AllSql.passportSql.update(vo,AllSql.passportSql.FIELD_DIAMOND,vo.diamond);
                    user.addUserData(UserDataEnum.DIAMOND,diamond,true,ReasonTypeEnum.GM);
                    byte level;
                    for(level = vo.vip;level<Model.VipBaseMap.size();level++){
                        VipBaseVo vipBaseVo = Model.VipBaseMap.get(level+1);
                        if(vipBaseVo == null)return;
                        if(vo.diamond >= vipBaseVo.vipLimit)
                            continue;
                        else
                            break;
                    }
                    System.out.println(level + "======" + vo.vip);
                    if(level>vo.vip){
                        vo.vip = level;
                        AllSql.passportSql.update(vo,AllSql.passportSql.FIELD_VIP,vo.vip);
                        new VipLevelUpRspd(client,vo.vip,vo.diamond,(byte)1);
                        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)level);
                        if(vipBaseVo == null)return;
                        user.addUserData(UserDataEnum.WormNestResetTime,vipBaseVo.duchongMapReset-oldVip.duchongMapReset,true);
                        user.addUserData(UserDataEnum.EXCHANGE_TILI_COUNT,vipBaseVo.buyTili-oldVip.buyTili,true);
                        user.addUserData(UserDataEnum.DRAGON_STONE_BUY_COUNT,vipBaseVo.dragonStoneCount-oldVip.dragonStoneCount,true);
                        user.addUserData(UserDataEnum.EXCHANGE_ARENA_COUNT,vipBaseVo.arenaBuyCount-oldVip.arenaBuyCount,true);
                        user.addUserData(UserDataEnum.NORMAL_RESET_COUNT,vipBaseVo.commMapReset-oldVip.commMapReset,true);
                        user.addUserData(UserDataEnum.MONEY_TREE_COUNT,vipBaseVo.moneytreeCount-oldVip.moneytreeCount,true);
                        user.addUserData(UserDataEnum.ELITE_BUY_COUNT,vipBaseVo.eliteByCount-oldVip.eliteByCount,true);
                        airing(client);
                        return;
                    }
                    new VipLevelUpRspd(client,vo.vip,vo.diamond,(byte)0);
                }
                break;
                case "/getloot":
                {
                    GetLootRqst lgRqst = new GetLootRqst();
                    lgRqst.stars.add((byte)1);
                    new GetLootCmd().execute(client, user,lgRqst,false);
                }
                break;
//                case "/clearmapcount":
//                {
//                    int mapID = Integer.parseInt(msgs[1]);
//                    ShortShortPVo ssp = user.mapEnteredMap.get((short)mapID);
////                    MapBaseVo mapBaseVo = Model.MapBaseMap.get(mapID);
////                    if(mapBaseVo == null)return;
//                    if(ssp == null){
//                        ssp=new ShortShortPVo();
//                        ssp.key=(short) mapID;
////                        ssp.value=(short)mapBaseVo.countLimit;
//                        ssp.value=0;
//                        user.mapEnteredMap.put(ssp.key,ssp);
//                    }
////                    ssp.value = (short)mapBaseVo.countLimit;
//                    ssp.value=0;
//                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + ShortShortPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
//                }
                case "/clearmapcount":
                {
                    if(user.mapEnteredMap.size() > 0) {
                        user.mapEnteredMap.clear();
                    }
                    new MapEnterTimesRspd(client,user.mapEnteredMap.values());
                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");
                }
                break;
                case "/dl":
                {

                    int groupID = Integer.parseInt(msgs[1]);
                    ArrayList<PropPVo> props=  BaseModel.getDropProps(groupID,user.baseID);
                    user.propModel.addListToBag(props,ReasonTypeEnum.GM);
                }
                break;
                case "/addegg":
                {
                    int propID = Integer.parseInt(msgs[1]);
                    PropBaseVo propBaseVo = Model.PropBaseMap.get(propID);
                    if(propBaseVo == null)return;
                    user.cacheUserVo.dragonEggModel.addDragonEgg(propBaseVo);
                }
                break;
                case "/startXSHD":
                {
                 // new RP_GoMapCmd().startRoom();
                }
                break;
                case "/addGW":
                {
                    int level = Integer.parseInt(msgs[1]);
                    int count = Integer.parseInt(msgs[2]);
                    byte type =  Byte.parseByte(msgs[3]);
                    int mapID = Integer.parseInt(msgs[4]);
                    int npcID = Integer.parseInt(msgs[5]);
                    NpcLevelBaseVo levelBaseVo = Model.NpcLevelBaseMap.get(level);
                    if(levelBaseVo == null)return;
                    user.fightingLoot = new FightLootVo();
                    user.fightingLoot.mapID=mapID;
                    user.fightingLoot.relifeCount = 3;
                    user.fightingLoot.totalExp += levelBaseVo.exp * count;
                    ArrayList<NpcDropPVo> allItems = new ArrayList<>();
                    int[] dropGroup = null;
                    switch (type) {
                        case 0:
                            dropGroup = levelBaseVo.commDropGroup;
                            break;
                        case 1:
                            dropGroup = levelBaseVo.eliteDropGroup;
                            break;
                        case 2:
                            dropGroup = levelBaseVo.bossDropGroup;
                            break;
                    }
                    for(int i=0;i<count;i++){
                        NpcDropPVo npcDropPVo = new NpcDropPVo();
                        npcDropPVo.items = new ArrayList<>();
                        npcDropPVo.npcID = npcID;
                        allItems.add(npcDropPVo);
                        npcDropPVo.items.addAll(BaseModel.getDropProps(dropGroup, user.baseID));
                        user.fightingLoot.totalProps.addAll(npcDropPVo.items);
                    }
                    new AllLootRspd(client,user.fightingLoot.totalExp,allItems);
                }
                break;
                case "/delegg":
                {
                    byte tempID =  Byte.parseByte(msgs[1]);
                    DragonEggModel dragonEggModel = user.cacheUserVo.dragonEggModel;
                    if(!dragonEggModel.touchMap.containsKey(tempID))return;
                    DragonEggVo eggVo = dragonEggModel.getEggByTempID(tempID);
                    if(eggVo == null)return;
                    dragonEggModel.dragonEggList.remove(eggVo);
                    dragonEggModel.touchMap.remove(eggVo.tempID);
                    dragonEggModel.saveSqlData();
                    if(user.cacheUserVo.gang.gangVo == null)return;
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    for(GangUserVo gangUserVo : gangVo.users.values()){
                        if(gangUserVo.cacheUserVo.guid == user.guid)continue;
                        if(gangUserVo.cacheUserVo.dragonEggModel.nowGangUserEggSet.contains(user.guid)){
                            gangUserVo.cacheUserVo.dragonEggModel.theirsEggUpSet.add(user.guid);
                        }
                    }
                }
                break;
                case "/addhours":
                {
                    client.addHours += Integer.parseInt(msgs[1]);
                    new ServerTimeUpdateRspd(client,JkTools.getGameServerTime(client));
                }
                break;
                case "/clearskill":
                    user.cacheUserVo.skillModel.skills.clear();
                    user.cacheUserVo.skillModel.saveSqlData();
                    break;
                case "/allstars":
                    user.setUserData(UserDataEnum.BAG_CELL_CD_COUNT,Model.BagBaseMap.size(),true);
                    user.setUserData(UserDataEnum.BAG_CELL_HAVE_COUNT,Model.BagBaseMap.size(),true);
                    user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL,-1,true);
                    user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME,-1,true);
                    break;
                case "/setall":
                    int id = Integer.parseInt(msgs[1]);
                    GMBaseVo baseVo = Model.GMBaseMap.get(id);
                    //等级
                    user.setUserData(UserDataEnum.LEVEL,baseVo.level,true);
                    //关卡开启
                    MapBaseVo mapBaseVo= Model.MapBaseMap.get(baseVo.pass);
                    while (mapBaseVo!=null) {
                        MapQualityPVo qualityVo = new MapQualityPVo();
                        qualityVo.ID = mapBaseVo.ID;
                        qualityVo.stars.add((byte)1);
                        user.mapQualityMap.put(qualityVo.ID, qualityVo);
                        new UpdateMapQualityRspd(client, qualityVo);

                        if(mapBaseVo.needMap == null || mapBaseVo.needMap[0]==0)break;
                        mapBaseVo= Model.MapBaseMap.get(mapBaseVo.needMap[0]);

                    }
                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPQUALITY, "'" + MapQualityPVoJoin.instance.joinCollection(user.mapQualityMap.values()) + "'");
                    //添加道具
                    user.propModel.addListToBag(baseVo.prop,ReasonTypeEnum.GM);
                    //装备
                    for(int propID : baseVo.equip){
                        PropPVo propPVo = new PropPVo();
                        propPVo.baseID = propID%1000 +user.baseID*1000;
                        propPVo.count = 1;
                        AllSql.propSql.insertNew(user,propPVo,PropCellEnum.EQUIP);
                        PropBaseVo propBaseVo = Model.PropBaseMap.get(propID);
                        PropPVo oldEquipVo = user.propModel.getEquipByIndex((byte) propBaseVo.type);
                        if (oldEquipVo != null) {
                            AllSql.propSql.update(AllSql.propSql.FIELD_TABLEID, PropCellEnum.BAG + "", oldEquipVo.tempID);
                            user.propModel.addInBag(oldEquipVo, false,true,ReasonTypeEnum.USE_BAG_PROP);
                        }
                        user.propModel.setEquipByIndex((byte) propBaseVo.type, propPVo);
                    }
//                    //皮肤
//                    int skinID = baseVo.skin;
//                    if(skinID != 0){
//                        skinID = skinID%10+user.baseID*10;
//                        if(!user.skinModel.skinList.contains(skinID)){
//                            user.skinModel.skinList.add((short)skinID);
//                            user.skinModel.saveSqlData();
//                        }
//                        SkinChangeRqst skinChangeRqst = new SkinChangeRqst();
//                        skinChangeRqst.baseID = skinID;
//                        new SkinChangeCmd().execute(client,user,skinChangeRqst);
//                    }
                    //背包格子数
                    int count = baseVo.bagCell;
                    user.setUserData(UserDataEnum.BAG_CELL_CD_COUNT,Math.min(Model.BagBaseMap.size(),count+3),true);
                    user.setUserData(UserDataEnum.BAG_CELL_HAVE_COUNT,count,true);
                    user.setUserData(UserDataEnum.NEXTTIME_BAG_CELL,-1,true);
                    user.setUserData(UserDataEnum.BAG_REMOVE_CD_TIME,-1,true);
                    //关闭引导
                    if(!"".equals(baseVo.guide)){
                        user.guide = baseVo.guide;
                        AllSql.userSql.update(user,AllSql.userSql.FIELD_GUIDE,"'"+baseVo.guide+"'");
                        new GuideRspd(client,user.guide);
                    }
                    //技能等级
                    int skillLevel = baseVo.skillLevel;
                    if(skillLevel > 0){
                        RoleBaseVo roleBaseVo = Model.RoleBaseMap.get((int)user.baseID);
                        if(roleBaseVo == null)return;
                        for(int skill : roleBaseVo.skills){
                            ArrayList<SkillBaseVo> skillList = Model.SkillBaseMap.get(skill);
                            if(skillList==null)return;
                            int level = 0;
                            if(skillList.size() == 1 || skillList.get(0).type == 1){
                                level =1;
                            }
                            for(SkillBaseVo vo : skillList){
                                if(level >= skillLevel)break;
                                level++;
                            }
                            if(level > 0){
                                SkillPVo skillPVo = new SkillPVo();
                                skillPVo.baseID = skill;
                                skillPVo.level = (byte)level;
                                user.cacheUserVo.skillModel.skills.put(skill,skillPVo);
                            }
                        }
                    }
                    //图腾
                    if(baseVo.talentLevel>0){
                        for(int talentID : Model.TalentBaseMap.keySet()){
                            TalentPVo talentPVo = new TalentPVo();
                            talentPVo.baseID = (byte)talentID;
                            talentPVo.level = (byte)baseVo.talentLevel;
                            user.cacheUserVo.skillModel.talents.put(talentPVo.baseID,talentPVo);
                        }
                    }
                    user.cacheUserVo.skillModel.saveSqlData();
                    break;
                case "/chuzhan":
                    short dragonID = Short.parseShort(msgs[1]);
                    DragonCacheModel dragonCacheModel = user.cacheUserVo.dragonCacheModel;
                    if(!dragonCacheModel.dragonsMap.containsKey(dragonID)){
                        DragonPVo dragonPvo =new DragonPVo();
                        dragonPvo.baseID=dragonID;
                        dragonPvo.level=0;
                        dragonPvo.isActive = true;
                        dragonPvo.count=0;
                        dragonCacheModel.dragonsMap.put(dragonPvo.baseID,dragonPvo);
                        dragonCacheModel.saveSqlData();
                        new AddDragonRspd(user.client,dragonPvo);
                    }
                    DragonSelectRqst dragonSelectRqst = new DragonSelectRqst();
                    dragonSelectRqst.baseID = dragonID;
                    new DragonSelectCmd().execute(client,user,dragonSelectRqst);
                    break;
                case "/guanjun":
                    RednessLoopModel.recordRednessRank();
                    if(RednessModel.campUserPVo != null){
                        HashMap<Long,Integer> rankMap = LoopSendMailModel.rankUsers.get(RednessModel.lastMailID);
                        int myLastIndex = -1;
                        if(rankMap != null && rankMap.containsKey(user.guid))myLastIndex = rankMap.get(user.guid);
                        new RP_LastRednessWinnerRspd(client,RednessModel.campUserPVo,RednessModel.lastRednessRank,myLastIndex);
                    }else{
                        CampUserPVo campUserPVo = new CampUserPVo();
                        campUserPVo.avata = new AvatarPVo();
                        new RP_LastRednessWinnerRspd(client,campUserPVo,RednessModel.lastRednessRank,-1);
                    }
                    break;
                case "/gdata":
                    byte gtype = Byte.parseByte(msgs[1]);
                    int gvalue = Integer.parseInt(msgs[2]);
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    if(gangVo == null)return;
                    if(!gangVo.users.containsKey(user.guid))return;
                    GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
                    if(vo == null)return;
                    vo.setGangUserData(gtype,gvalue,true);
                    break;
                case "/setgiftcanget":
                    GangVo gVo = user.cacheUserVo.gang.gangVo;
                    if(gVo == null)return;
                    if(!gVo.users.containsKey(user.guid))return;
                    GangUserVo gangUserVo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
                    if(gangUserVo == null)return;
                    gangUserVo.isGetGift = 0;
                    AllSql.gangMemberSql.update(gangUserVo,AllSql.gangMemberSql.FIELD_IS_GET_GIFT,0);
                    gVo.createGangInfoRspd(client);
                    break;
                case "/cleargetbox":
                    GangVo gvo = user.cacheUserVo.gang.gangVo;
                    if(gvo == null)return;
                    if(!gvo.users.containsKey(user.guid))return;
                    GangUserVo userVo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
                    if(userVo == null)return;
                    userVo.getBoxID.clear();
                    new GetBoxIDRspd(client,userVo.getBoxID);
                    userVo.saveGetBoxID();
                    break;
                case "/addluck":
                    GangVo gangVo1 = user.cacheUserVo.gang.gangVo;
                    if(gangVo1 == null)return;
                    if(!gangVo1.users.containsKey(user.guid))return;
                    GangUserVo gangUserVo1 = user.cacheUserVo.gang.gangVo.users.get(user.guid);
                    if(gangUserVo1 == null)return;
                    gangUserVo1.luckValue += Integer.parseInt(msgs[1]);
                    AllSql.gangMemberSql.update(gangUserVo1,AllSql.gangMemberSql.FIELD_LUCK_VALUE,gangUserVo1.luckValue);
                    user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
                    break;
                case "/addsystemminute":
                    Model.addMinute += Integer.parseInt(msgs[1]);
                    for(AbsClient absClient : Client.allOnline.values()){
                        Client client1=(Client)absClient;
                        new ServerTimeUpdateRspd(client1,JkTools.getGameServerTime(client1));
                    }
                    Calendar calendar = JkTools.getCalendar();
                    calendar.add(Calendar.HOUR_OF_DAY,user.client.addHours);
                    int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    user.nextUpdataTime = UserUpdateModel.nextUpdateTime(now);
                    SystemLoopModel.checkUpdate();
                    break;
                case "/delphone":
                    CachePassportVo.useTelphone.remove(msgs[1]);
                    break;
                case "/activitytime":
                    int startID = Integer.parseInt(msgs[1]);
                    int startMinute = Integer.parseInt(msgs[2]);
                    SystemUpdateBaseVo startVo = Model.SystemUpdateBaseMap.get(startID);
                    startVo.minute = startMinute;
                    if(msgs.length > 3){
                        int endID = Integer.parseInt(msgs[3]);
                        int endMinute = Integer.parseInt(msgs[4]);
                        SystemUpdateBaseVo endVo = Model.SystemUpdateBaseMap.get(endID);
                        endVo.minute = endMinute;
                    }
                    SystemLoopModel.init();
                    break;
                case "/sendarena":
                    ArenaModel.recordArenaReward();
                    break;
                case "/rare":
                    ArrayList<SkillUserPVo> users = new ArrayList<>();
                    for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
                        if(cacheUserVo.guid == user.guid)continue;
                        users.add(UserVoAdapter.toSkillUserPVo(cacheUserVo));
                        if(users.size() >= 3)break;
                    }
                    ArrayList<PropPVo> list = new ArrayList<>();
                    new MR_LootStartRspd(client,(byte)103,users,0,list);
                    new GoMapCmd().execute(client, user, 13901, true);
                    break;
                case "/emperor":
                    EmperorAwakenRqst rqst1 = new EmperorAwakenRqst();
                    rqst1.id = Byte.parseByte(msgs[1]);
                    new EmperorAwakenCmd().execute(client,user,rqst1);
                    break;
                case "/useprop":
                    UseBagPropRqst useBagPropRqst = new UseBagPropRqst();
                    int propID = Integer.parseInt(msgs[1]);
                    for(PropPVo propPVo : user.propModel.bagItems.values()){
                        if(propPVo.baseID == propID){
                            useBagPropRqst.tempID = propPVo.tempID;
                            break;
                        }
                    }
                    if(useBagPropRqst.tempID == 0)break;
                    useBagPropRqst.count = 1;
                    new UseBagPropCmd().execute(client,user,useBagPropRqst);
                    break;
            }
        }
    }

    private static void airing(Client client){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(5);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int index = client.passportVo.vip;
        if(index%airingVo.divisor != 0)return;
        if(JkTools.compare(index,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&&JkTools.compare(index,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        pVo.msg = airingVo.msg.replace("{1}",client.currentUser.cacheUserVo.name).replace("{2}",String.valueOf(index));
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}
