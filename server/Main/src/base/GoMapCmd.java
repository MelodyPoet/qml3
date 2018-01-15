package base;

import comm.*;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import mine.MineRoomVo;
import prop.PropModel;
import protocol.*;
import redness.RP_RoomVo;
import sqlCmd.AllSql;
import table.*;
import utils.UserVoAdapter;

import java.lang.reflect.Array;
import java.util.*;


public class GoMapCmd extends BaseRqstCmd {
public  boolean mapAttackMode=false;
    boolean unlimitTest=false;
    public GoMapCmd() {}
    public GoMapCmd( boolean mapAttackMode) {
        this.mapAttackMode=mapAttackMode;
    }

    @Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        GoMapRqst rqst=(GoMapRqst) baseRqst;
        if(rqst.mapID==996)return;
 		execute(client,user,rqst.mapID,true);
	}
    public boolean execute(Client client,User user, int mapID,boolean needJumpMap) {


        MapBaseVo mapBaseVo = Model.MapBaseMap.get(mapID);

        if (mapBaseVo == null) {
            return false;
        }
        if (mapBaseVo.type == MapTypeEnum.TWOER) {return false;}
        if(mapBaseVo.type == MapTypeEnum.NEWGAME_FIGHT && user.actState != UserActState.xshdFighing)return false;

            if(mapBaseVo.needLevel>user.userdata[UserDataEnum.LEVEL]){

                return false;

        }

        if(mapBaseVo.type == MapTypeEnum.NORMAL&&mapBaseVo.needMap!=null&&mapBaseVo.needMap[0]>0){
            for(int needMap: mapBaseVo.needMap){
                if(!user.mapQualityMap.containsKey(needMap))return false;
            }
        }
        if (mapBaseVo.type == MapTypeEnum.NORMAL ) {
            if(mapAttackMode==false || MapAttackCmd.isCheck == 1){
                if(user.propModel.checkBagIsFull()){
                    return false;
                }
                if (user.enoughForcostUserDataAndProp(UserDataEnum.TILIZHI, mapBaseVo.energe) == false) {
                    return false;
                }
            }

            //  DCGameLog.getInstance().AddLog(GameLogType.COINLOST, new String[]{user.guid + "", UserDataEnum.TILIZHI + "", (mapBaseVo.energe) + "", user.getUserData(UserDataEnum.TILIZHI) + "", "GoMap", user.getUserData(UserDataEnum.LEVEL) + ""});
        }
        if(unlimitTest==false) {
            if (mapBaseVo.countLimit != 0) {
                short keyMapID = (short) mapBaseVo.ID;
                if (mapBaseVo.countLimit < 0) {
                    keyMapID = (short) -mapBaseVo.countLimit;
                }
                MapEnteredPVo sspvo = user.mapEnteredMap.get(keyMapID);
                if (sspvo == null) {
                    sspvo = new MapEnteredPVo();
                    sspvo.key = keyMapID;
                    sspvo.value = 0;
                    sspvo.time = 0;
                    user.mapEnteredMap.put(keyMapID, sspvo);
                }
                MapBaseVo groupMap = mapBaseVo;
                if (mapBaseVo.countLimit < 0) groupMap = Model.MapBaseMap.get((int) keyMapID);
                if (groupMap.countLimit <= sspvo.value) {
                    return false;
                }
                if (JkTools.getGameServerTime(client) - sspvo.time < 0) return false;
                if(JkTools.indexOf(Model.GameSetBaseMap.get(66).intArray,groupMap.type) < 0){
                    sspvo.value++;

                    AllSql.userSql.update(user, AllSql.userSql.FIELD_MAPENTERED, "'" + MapEnteredPVoJoin.instance.joinCollection(user.mapEnteredMap.values()) + "'");

                }
            }
        }
        if(mapBaseVo.type == MapTypeEnum.REDNESS){
            RP_RoomVo roomVo = user.rednessModel.myRoom;
            roomVo.guestReady = false;
            roomVo.isInGame = 1;
        }
        if(mapBaseVo.type == MapTypeEnum.GANG){
            GangVo gangVo = user.cacheUserVo.gang.gangVo;
            if(gangVo == null)return false;
            GangUserVo gangUserVo = gangVo.users.get(user.guid);
            if(gangUserVo == null)return false;
            int count = gangUserVo.getGangUserData(GangUserDataEnum.MAP_COUNT);
            if(count <= 0)return false;
            if(mapBaseVo.needMap!=null&&mapBaseVo.needMap[0]>0){
                HashMap<Short,GangMapPVo> map = gangVo.mapModel.gangAllMap;
                GangMapBaseVo gangMapVo = Model.GangMapBaseMap.get(mapBaseVo.ID);
                if(map.containsKey((short)gangMapVo.baseMap)){
                    GangMapPVo gangMapPVo = map.get((short)gangMapVo.baseMap);
                    if(gangMapPVo.bossBlood == 0)return false;
                    if(gangMapPVo.mapID != mapBaseVo.ID)return false;
                }
                for(int needMap: mapBaseVo.needMap){
                    GangMapPVo gangMapPVo = null;
                    if(!map.containsKey((short)needMap)){
                        GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get(needMap);
                        if(!map.containsKey((short)gangMapBaseVo.baseMap))return false;
                        gangMapPVo = map.get((short)gangMapBaseVo.baseMap);
                    }else{
                        gangMapPVo = map.get((short)needMap);
                    }
                    if(gangMapPVo.level<=0)return false;
                }
            }
            gangUserVo.setGangUserData(GangUserDataEnum.MAP_COUNT,count-1,true);
            user.propModel.addListToBag(Model.GameSetBaseMap.get(41).intArray,ReasonTypeEnum.GO_MAP);
        }
//        if(mapBaseVo.jumpRate!=null){
//           int mapIndex= JkTools.getRandRange(mapBaseVo.jumpRate,100,2);
//            if(mapIndex>-1){
//                mapBaseVo= Model.MapBaseMap.get(mapBaseVo.jumpRate[mapIndex+1]);
//            }
//
//        }
        realDo(client,user,mapBaseVo,needJumpMap);
        return true;
    }
    public   void realDo(Client client,User user,MapBaseVo mapBaseVo,boolean needJumpMap){
        ArrayList<CampUserPVo> campUserPVos = new ArrayList<>();
        user.currentMap = mapBaseVo;
        if (mapBaseVo.type != MapTypeEnum.CAMP) {

            if (mapBaseVo.type == MapTypeEnum.TWOER) {
//user.model
            }else if(mapBaseVo.type == MapTypeEnum.MINERAL_PRIVATE){
                if(!GameSetModel.checkTime(15)){
                    new GoMapCmd().execute(client, user, 1, true);
                    return;
                }
                int campUserSize = Model.mineCampUsers.size();
                if (campUserSize >= 20) {
                    Long guid = (Long) Model.mineCampUsers.keySet().toArray()[new Random().nextInt(campUserSize)];
                    Model.mineCampUsers.remove(guid);
                }
//                for (CacheUserVo u : Model.mineCampUsers.values()) {
//                    if (u == user.cacheUserVo) continue;
//                    campUserPVos.add(UserVoAdapter.toCampUserPVo(u));
//                }
//                for (CacheUserVo u : CacheUserVo.allMap.values()) {
//                    if (u == user.cacheUserVo) continue;
//                    campUserPVos.add(UserVoAdapter.toCampUserPVo(u));
//                    if(campUserPVos.size() >= 20)break;
//                }
            }else if(mapBaseVo.type == MapTypeEnum.MINERAL_PUBLIC){
                if(!GameSetModel.checkTime(17)){
                    new GoMapCmd().execute(client, user, 1, true);
                    return;
                }
                MineRoomVo roomVo = user.cacheUserVo.mineModel.myRoom;
                if(roomVo == null){
                    user.cacheUserVo.mineModel.joinRoom();
                    roomVo = user.cacheUserVo.mineModel.myRoom;
                }
                int now = JkTools.getGameServerTime(null);
                Iterator<Map.Entry<Long,CacheUserVo>> it = roomVo.roomUsers.entrySet().iterator();
//                while (it.hasNext()){
//                    CacheUserVo u = it.next().getValue();
//                    if (u == user.cacheUserVo) continue;
//                    if(now - u.mineModel.lastTime > 5*60){
//                        u.mineModel.myRoom = null;
//                        it.remove();
//                        continue;
//                    }
//                    campUserPVos.add(UserVoAdapter.toCampUserPVo(u));
//                }
                user.cacheUserVo.mineModel.lastTime = JkTools.getGameServerTime(null);
//                for (CacheUserVo u : CacheUserVo.allMap.values()) {
//                    if (u == user.cacheUserVo) continue;
//                    campUserPVos.add(UserVoAdapter.toCampUserPVo(u));
//                    if(campUserPVos.size() >= 20)break;
//                }
            }else{
                user.fightingLoot = new FightLootVo();
                user.fightingLoot.mapID=mapBaseVo.ID;
                user.fightingLoot.relifeCount = mapBaseVo.relifeCount;
                VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
                if(vipBaseVo==null)return;
                user.fightingLoot.freeRelifeCount = 0;
                if(user.gangSkillModel.gangSkillList.size()>0){
                    GangSkillMsgPVo expPVo = user.gangSkillModel.gangSkillMap.get(GangSkillEnum.EXP);
                    if(expPVo == null)return;
                    user.fightingLoot.expAddRatio = expPVo.effect;
                    GangSkillMsgPVo moneyPVo = user.gangSkillModel.gangSkillMap.get(GangSkillEnum.MONEY);
                    if(moneyPVo == null)return;
                    user.fightingLoot.moneyAddRatio = moneyPVo.effect;
                }else{
                    user.fightingLoot.expAddRatio = 0;
                    user.fightingLoot.moneyAddRatio = 0;
                }
                ArrayList<NpcLayoutBaseVo> npcList = Model.NpcLayoutBaseMap.get(mapBaseVo.ID);

                ArrayList<NpcDropPVo> allItems = new ArrayList<>();
                short dropTempID = 1;
                Random rnd = new Random();
                MissionBaseVo dropMission = null;

                for (MissionBaseVo msVo : user.missionModel.acceptedList.keySet()) {
                    if (msVo.conditionType == MissionConditionEnum.COLLECT && msVo.goMap == mapBaseVo.ID) {
                        dropMission = msVo;
                        break;
                    }

                }
//                if(user.cacheUserVo.level <= Model.gameSetBaseVo.dropEquip[0] && mapBaseVo.ID <= Model.gameSetBaseVo.dropEquip[1]){
//                    int i=-1;
//                    PropPVo pVo = null;
//                    for(PropPVo propPVo : user.cacheUserVo.equipItems){
//                        i++;
//                        if(i == 0){
//                            continue;
//                        }
//                        if(propPVo == null){
//                            boolean flag = false;
//                            for(PropPVo bagPVo : user.propModel.bagItems.values()){
//                                if(PropModel.isEquip(bagPVo.baseID)){
//                                    if(bagPVo.baseID/10%10== i-1){
//                                        flag = true;
//                                        break;
//                                    }
//                                }
//                            }
//                            if(flag){
//                                System.out.println("=====BAG HAVE=====");
//                                continue;
//                            }
//                            user.fightingLoot.equipID = (byte) (i-1);
//                            user.fightingLoot.equipLevel = (byte) -1;
//                            System.out.println("=====BAG NONE=====" + i);
//                            break;
//                        }
//                        if(pVo == null){
//                            pVo = propPVo;
//                        }else{
//                            if(pVo.baseID/100%10>propPVo.baseID/100%10 || pVo.baseID%10 > propPVo.baseID%10){
//                                pVo = propPVo;
//                            }
//                        }
//                    }
//                    if(pVo != null && user.fightingLoot.equipID == -1){
//                        System.out.println("=====LEVEL MIN=====");
//                        user.fightingLoot.equipID = (byte) (pVo.baseID/10%10);
//                        user.fightingLoot.equipLevel = (byte)(pVo.baseID/100%10);
//                        user.fightingLoot.equipQuality = (byte)(pVo.baseID%10);
//                    }
//                    System.out.println("==========EQUIP==========="+user.fightingLoot.equipID +"=============="+user.fightingLoot.equipLevel+"=============="+user.fightingLoot.equipQuality);
//                }
                if (npcList != null)
                    for (Iterator<NpcLayoutBaseVo> iter = npcList.iterator(); iter.hasNext(); ) {
                        NpcLayoutBaseVo layout = iter.next();
//                        for (int j = 0; j < layout.summonCount; j++) {
//                            iter.next();
//                        }
                        NpcBaseVo npc = Model.NpcBaseMap.get(layout.npcID);
                        NpcLevelBaseVo lvlVo = Model.NpcLevelBaseMap.get(layout.level);
//                        if(npc.type==MapItemEnum.SCENE_PROP){
//                            for (int i = 0, len = 3; i < len; i++) {
//                                dropTempID = addPropsBuyNpc(layout, lvlVo, npc, user, allItems, dropMission, dropTempID, rnd);
//                            }
//                        }else {
                            if(layout.pos!=null) {
                                for (int i = 0, len = layout.pos.length / 3; i < len; i++) {
                                 //   dropTempID = addPropsBuyNpc(layout, lvlVo, npc, user, allItems, dropMission, dropTempID, rnd);


                                }
                            }
//                        }
                    }
                if (mapBaseVo.finalBox > 0) {
//                    dropTempID = addPropsBuyNpc(null,Model.NpcLevelBaseMap.get(1), Model.NpcBaseMap.get(mapBaseVo.finalBox),
//                            user, allItems, dropMission, dropTempID, rnd);
                    int[] firstDrop = mapBaseVo.firstDrop;
                    if(mapBaseVo.dropGroup != null || firstDrop != null || mapBaseVo.talentExp > 0){
                        NpcDropPVo npcDropPVo = new NpcDropPVo();
                        npcDropPVo.npcID = mapBaseVo.finalBox;
                        npcDropPVo.items = new ArrayList<>();
                        if(mapBaseVo.dropGroup != null){
                            ArrayList<PropPVo> props = BaseModel.getDropProps(mapBaseVo.dropGroup,user.baseID);
                            for(PropPVo propPVo : props){
                                propPVo.tempID=0;
//                            if(propPVo.baseID == UserDataEnum.MONEY){
//                                propPVo.count *= mapBaseVo.money;
//                                if(user.fightingLoot.moneyAddRatio>0){
//                                    propPVo.count+=(propPVo.count * user.fightingLoot.moneyAddRatio/10+5)/10;
//                                }
//                            }
                                npcDropPVo.items.add(propPVo);
                                user.fightingLoot.totalProps.add(propPVo);
                            }
                        }
                       if (!user.mapQualityMap.containsKey(mapBaseVo.ID) && firstDrop != null){
                           for(int i=0;i<firstDrop.length;i+=2){
                               if(firstDrop[i] == UserDataEnum.EXP){
                                   user.fightingLoot.totalExp += firstDrop[i+1];
                                   continue;
                               }
                               PropPVo propPVo = new PropPVo();
                               propPVo.baseID = firstDrop[i];
                               propPVo.count = firstDrop[i+1];
                               npcDropPVo.items.add(propPVo);
                               user.fightingLoot.totalProps.add(propPVo);
                           }
                       }

                        if(mapBaseVo.talentExp > 0){
                            PropPVo propPVo = new PropPVo();
                            propPVo.baseID = UserDataEnum.TALENT_EXP;
                            propPVo.count = mapBaseVo.talentExp;
                            npcDropPVo.items.add(propPVo);
                            user.fightingLoot.totalProps.add(propPVo);
                        }
                        allItems.add(npcDropPVo);
                    }
                }
                if(user.mapQualityMap.containsKey(mapBaseVo.ID) && mapBaseVo.exp>0){
                    user.fightingLoot.totalExp += mapBaseVo.exp;
                }
//                if(user.fightingLoot.equipPVo != null){
//                    System.out.println("========CHANGE===="+user.fightingLoot.equipPVo.baseID+"======TO====="+(user.fightingLoot.equipPVo.baseID + (user.fightingLoot.equipID -user.fightingLoot.equipPVo.baseID/10%10)*10));
//                    user.fightingLoot.equipPVo.baseID = user.fightingLoot.equipPVo.baseID + (user.fightingLoot.equipID -user.fightingLoot.equipPVo.baseID/10%10)*10;
//                }
                if(user.fightingLoot.expAddRatio>0){
                    user.fightingLoot.totalExp += (user.fightingLoot.totalExp * user.fightingLoot.expAddRatio/100+5)/10;
                }
            if(mapAttackMode==false) {
              //  new AllLootRspd(client, user.fightingLoot.totalExp,allItems);
             //   user.actState = UserActState.FIGHT;


           // AllSql.userSql.saveFightData(user);
            }
            }
          } else  {
            user.actState = UserActState.CAMP;
//            if(user.udpClient!=null&&user.udpClient.currentScene!=null) {
//                user.udpClient.currentScene.exitScene();
//            }
// new CampHeroEnterRspd(null, UserVoAdapter.toCampUserPVo(user.cacheUserVo));
            //CastModel.castCamp(client);

          //  int count = 0;
//            for (Client cl :Client.allOnline.values()){
//                if(cl!=client&&cl.currentUser!=null&&cl.currentUser.currentMap.type== MapTypeEnum.CAMP){
//                    if(count++>10)return;
//                    campUserPVos.add(UserVoAdapter.toCampUserPVo(cl.currentUser.cacheUserVo));
//                    //new CampHeroEnterRspd(client, UserVoAdapter.toCampUserPVo(cl.currentUser.cacheUserVo));
//
//                };
//
//            }
            int count = 0;
//            for (CacheUserVo u : Model.campUsers.values()) {
//
//                if (u == user.cacheUserVo) continue;
//
//                campUserPVos.add(UserVoAdapter.toCampUserPVo(u));
//
//            }

        }


        if(mapAttackMode==false) {
            user.propModel.trampShop = null;
            int trampNpcID=0;
            if(mapBaseVo.trampNpcID!=null){
                int trampIndex= JkTools.getRandRange(mapBaseVo.trampNpcID,1000,2);
                System.out.println(trampIndex);
                if(trampIndex>-1)trampNpcID=mapBaseVo.trampNpcID[trampIndex+1];
            }

            if (needJumpMap) {
//                if(mapBaseVo.type == MapTypeEnum.CAMP){
//                    if(ChangeMainCmd.flag){
//                        new GoMapRspd(client, 3, campUserPVos);
//                    }else{
//                        new GoMapRspd(client, 1, campUserPVos);
//                    }
//                }else{

                    new GoMapRspd(client, mapBaseVo.ID,trampNpcID);
                    if(mapBaseVo.mmoMode==1) {
                        MltSceneEnterRqst rqst = new MltSceneEnterRqst();
                        rqst.protocolID = MltSceneEnterRqst.PRO_ID;

                        //rqst.fromBytes();

                        rqst.mapID = mapBaseVo.ID;

                        rqst.user = UserVoAdapter.toSkillUserPVo(user.cacheUserVo);
                        rqst.userAttributes= JkTools.intArrayAsList( user.heroAttributes);
                        //rqst.user.posx = 70000;
                        //rqst.user.posz = -280000;
                        rqst.sendToGate(client);
                    }

//                }
            }
        }


    }

    public   void realDoToTower(Client client,User user,int towerID,int currentFloor,boolean needJumpMap) {
        TowerBaseVo myTower=Model.TowerBaseMap.get(towerID).get(currentFloor);
        MapBaseVo mapBaseVo = Model.MapBaseMap.get(myTower.mapID);
        user.fightingLoot = new FightLootVo();
        user.fightingLoot.mapID = mapBaseVo.ID;
        user.fightingLoot.relifeCount = mapBaseVo.relifeCount;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        user.fightingLoot.freeRelifeCount = 0;
        ArrayList<CampUserPVo> campUserPVos = new ArrayList<>();
        for(int i=0;i<myTower.rewardProp.length;i+=2){
            if(myTower.rewardProp[i] == UserDataEnum.EXP){
                user.fightingLoot.totalExp += myTower.rewardProp[i+1];
            }else{
                PropPVo propPVo = new PropPVo();
                propPVo.baseID = myTower.rewardProp[i];
                propPVo.count = myTower.rewardProp[i+1];
                if(propPVo.baseID == UserDataEnum.MONEY && user.fightingLoot.moneyAddRatio>0)
                    propPVo.count+=(propPVo.count * user.fightingLoot.moneyAddRatio/100+5)/10;
                user.fightingLoot.totalProps.add(propPVo);
            }
        }
        if (mapAttackMode == false) {
            user.currentMap = mapBaseVo;
            if (needJumpMap) {
                new GoMapRspd(client, mapBaseVo.ID,0);
            }
        }
    }

    private short addPropsBuyNpc(NpcLayoutBaseVo layoutBaseVo, NpcLevelBaseVo lvlVo,NpcBaseVo npc, User user, ArrayList<NpcDropPVo> allItems, MissionBaseVo dropMission,  short dropTempID,Random rnd) {

        NpcDropPVo npcDropPVo = new NpcDropPVo();
        npcDropPVo.items = new ArrayList<>();
        npcDropPVo.npcID = npc.ID;
        allItems.add(npcDropPVo);

        if(npc.type==MapItemEnum.SCENE_PROP){
            if(npc.extraParams!=null) {
                int rndSType = JkTools.getRandRange(npc.extraParams, 100, 2);
                if (rndSType >= 0) {
                    npcDropPVo.specialType = (byte) (rndSType / 2 + 1);
                    return dropTempID;
                }
            }
        }else{
            user.fightingLoot.totalExp += lvlVo.exp;

        }
//        System.out.println("==========!!!!!!!!!!!!====npcDropPVo==========="+npcDropPVo.npcID);
        ArrayList<PropPVo> props =new ArrayList<>();
        if(layoutBaseVo!=null) {
            boolean isDropEquip = true;
            boolean isDropMoney = true;
            if (layoutBaseVo.dropGroup != null) {
                for(int i : layoutBaseVo.dropGroup){
                    switch (i){
                        case -1:
                            isDropEquip = false;
                            isDropMoney = false;
                            break;
                        case -2:
                            isDropEquip = false;
                            break;
                        case -3:
                            isDropMoney = false;
                            break;
                        default:
                            props.addAll(BaseModel.getDropProps(i, user.baseID));
                    }
                }
            }

            if(layoutBaseVo.isleveldrop==1){
                byte type = (byte) (layoutBaseVo.attributeType%10);
                if(type == 4){
                    props.addAll(BaseModel.getDropProps(lvlVo.eliteDropGroup,user.baseID));
                }else if(type == 5){
                    props.addAll(BaseModel.getDropProps(lvlVo.bossDropGroup,user.baseID));
                }else{
                    props.addAll(BaseModel.getDropProps(lvlVo.commDropGroup,user.baseID));
                }
            }

            Iterator<PropPVo> it = props.iterator();
            while (it.hasNext()){
                PropPVo propPVo = it.next();
                if(!isDropEquip){
                    if(PropModel.isEquip(propPVo.baseID)){
                        it.remove();
                        continue;
                    }
                }
                if(!isDropMoney){
                    if(propPVo.baseID == UserDataEnum.MONEY){
                        it.remove();
                        continue;
                    }
                }
            }

            if(user.currentMap.type == MapTypeEnum.ELITE){
                if(!user.eliteModel.elitePassedMap.contains((short)user.currentMap.ID)){
                    props.addAll(BaseModel.getDropProps(layoutBaseVo.fristDropGroup, user.baseID));
                }
            }else {
                if (layoutBaseVo.fristDropGroup != null && user.mapQualityMap.containsKey(layoutBaseVo.ID) == false) {
                    props.addAll(BaseModel.getDropProps(layoutBaseVo.fristDropGroup, user.baseID));
                }
            }
        }
//        if(dropMission!=null){
//            if(rnd.nextInt(10000)<dropMission.condition[2]){
//                PropPVo msItem=  new PropPVo();
//                msItem.baseID=dropMission.condition[0];
//                props.add(msItem);
//            }
//        }
        for (PropPVo propPVo : props) {
//            if(user.fightingLoot.equipID >=0){
//                if(PropModel.isEquip(propPVo.baseID)){
//                    if(propPVo.baseID/100%10>user.fightingLoot.equipLevel || propPVo.baseID%10>user.fightingLoot.equipQuality){
//                        System.out.println("========SET_PVO===="+propPVo.baseID);
//                        user.fightingLoot.equipPVo = propPVo;
//                    }
//                    if(propPVo.baseID/10%10 == user.fightingLoot.equipID && user.fightingLoot.equipPVo!=null && user.fightingLoot.equipPVo.baseID == propPVo.baseID){
//                        System.out.println("========HAVING===="+propPVo.baseID);
//                        user.fightingLoot.equipPVo = null;
//                        user.fightingLoot.equipID = -1;
//                    }
//                }
//            }

            if(propPVo.baseID == UserDataEnum.MONEY){
                if(user.fightingLoot.moneyAddRatio>0){
                    propPVo.count+=(propPVo.count * user.fightingLoot.moneyAddRatio/100+5)/10;
                }
            }
            npcDropPVo.items.add(propPVo);

            if(npc.type==MapItemEnum.SCENE_PROP){
                propPVo.tempID = dropTempID++;
                ArrayList<PropPVo> list = user.fightingLoot.dynamicTotalProps.get((short)propPVo.tempID);
                if(list == null){
                    list = new ArrayList<>();
                    user.fightingLoot.dynamicTotalProps.put((short) propPVo.tempID,list);
                }
                list.add(propPVo);

            }else {
                propPVo.tempID=0;
                user.fightingLoot.totalProps.add(propPVo);
            }
        }
return dropTempID;
    }

    public void executeAuto(Client client,User user, int mapID,boolean needJumpMap) {

        MapBaseVo mapBaseVo = Model.MapBaseMap.get(mapID);

        if (mapBaseVo == null) {
            return;
        }

        user.currentMap=mapBaseVo;
        if(mapBaseVo.type == MapTypeEnum.REDNESS){
            user.actState = UserActState.xshdFighing;
        }else{
            user.actState = UserActState.FIGHT;
        }


        if(needJumpMap) {
            new GoMapRspd(client, mapBaseVo.ID,0);
        }
    }
}
