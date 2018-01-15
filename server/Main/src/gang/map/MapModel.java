package gang.map;

import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.utils.JkTools;
import mail.LoopSendMailModel;
import mail.MailModel;
import protocol.*;
import table.GangMapBaseVo;
import table.GangUserDataEnum;
import table.MapBaseVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by admin on 2017/4/26.
 */
public class MapModel {
    public HashMap<Short,HashSet<Long>> mapUser;
    public HashMap<Short,GangMapPVo> gangAllMap;
    public HashMap<Short,HashMap<Long,GangMapAwardPVo>> userMapAward;
    public ByteIntPVo gangDragon;
    public int[] dragonWish;
    public GangVo gangVo;

    public MapModel(GangVo gangVo){
        this.gangVo = gangVo;
        mapUser = new HashMap<>();
        gangAllMap = new HashMap<>();
        userMapAward = new HashMap<>();
        gangDragon = new ByteIntPVo();
        dragonWish = new int[Model.GangDragonWishBaseMap.size()+1];
        for(int i=1;i<dragonWish.length;i++){
            dragonWish[i] = 100;
        }
    }

    public void wish(int id){
        switch (id){
            case 1:
            case 2:
            case 3:
                for(Map.Entry<Short,HashMap<Long,GangMapAwardPVo>> item : userMapAward.entrySet()){
                    HashMap<Long,GangMapAwardPVo> map = item.getValue();
                    int now = JkTools.getGameServerTime(null);
                    GangMapPVo gangMapPVo = gangAllMap.get(item.getKey());
                    GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get((int)gangMapPVo.mapID);
                    for(Map.Entry<Long,GangMapAwardPVo> mapItem : map.entrySet()){
                        GangMapAwardPVo gangMapAwardPVo = mapItem.getValue();
                        int time = now - gangMapAwardPVo.nextTime;
                        if(time < 0){
                            gangMapAwardPVo.nextTime = now+((gangMapBaseVo.limit*dragonWish[1]/100+(-time/gangMapBaseVo.coolTime+1)*gangMapBaseVo.count)/gangMapBaseVo.count*dragonWish[2]/100+1)*(gangMapBaseVo.coolTime*dragonWish[3]/100);
                        }else{
                            if(id == 1)
                                gangMapAwardPVo.nextTime = now+(gangMapBaseVo.limit*(dragonWish[1] - 100)/100/gangMapBaseVo.count*dragonWish[2]/100+1)*gangMapBaseVo.coolTime*dragonWish[3]/100;
                        }
                    }
                }
                break;
            case 4:
                for(GangUserVo gangUserVo : gangVo.users.values()){
                    gangUserVo.setGangUserData(GangUserDataEnum.MAP_COUNT,gangUserVo.getGangUserData(GangUserDataEnum.MAP_COUNT)+dragonWish[4]-100,true);
                }
                break;
        }
    }

    public static void fail(User user, MapBaseVo mapBaseVo,int bossBlood){
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GangUserVo gangUserVo = gangVo.users.get(user.guid);
        if(gangUserVo == null)return;
        MapModel mapModel = gangVo.mapModel;
        HashMap<Short,GangMapPVo> map = mapModel.gangAllMap;
        GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get(mapBaseVo.ID);
        short baseID = (short)gangMapBaseVo.baseMap;
        GangMapPVo gangMapPVo = null;
        if(map.containsKey(baseID)){
            gangMapPVo = map.get(baseID);
            if(gangMapPVo.mapID != mapBaseVo.ID)return;
            if(gangMapPVo.bossBlood <= bossBlood)return;
            gangMapPVo.bossBlood -= bossBlood;
            HashSet<Long> set = mapModel.mapUser.get(baseID);
            if(!set.contains(user.guid)){
                set.add(user.guid);
                gangMapPVo.userCount = (short) set.size();
            }
        }else{
            gangMapPVo = new GangMapPVo();
            gangMapPVo.baseID = baseID;
            gangMapPVo.mapID = (short)mapBaseVo.ID;
            gangMapPVo.bossBlood = gangMapBaseVo.bossBlood - bossBlood;
            gangMapPVo.level = (byte)0;
            gangMapPVo.userCount = 1;
            map.put(baseID,gangMapPVo);
            HashSet<Long> users = new HashSet<>();
            users.add(user.guid);
            gangVo.mapModel.mapUser.put(baseID,users);
        }
        gangVo.saveGangMap();
    }

    public static void success(User user, MapBaseVo mapBaseVo){
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GangUserVo gangUserVo = gangVo.users.get(user.guid);
        if(gangUserVo == null)return;
        MapModel mapModel = gangVo.mapModel;
        HashMap<Short,GangMapPVo> map = mapModel.gangAllMap;
        GangMapBaseVo gangMapBaseVo = Model.GangMapBaseMap.get(mapBaseVo.ID);
        GangMapBaseVo nextVo = Model.GangMapBaseMap.get(gangMapBaseVo.nextMap);
        short baseID = (short)gangMapBaseVo.baseMap;
        GangMapPVo gangMapPVo = null;
        if(map.containsKey(baseID)){
            gangMapPVo = map.get(baseID);
            if(gangMapPVo.mapID != mapBaseVo.ID){
                new ServerTipRspd(user.client,(short)303,null);
                return;
            }
            if(gangMapPVo.level == 0){
                nextVo = gangMapBaseVo;
            }
            if(nextVo != null){
                gangMapPVo.mapID = (short)nextVo.ID;
                gangMapPVo.bossBlood = nextVo.bossBlood;
                gangMapPVo.level = (byte) nextVo.level;
            }else{
                gangMapPVo.bossBlood = 0;
            }
            HashSet<Long> set = mapModel.mapUser.get(baseID);
            if(!set.contains(user.guid)){
                set.add(user.guid);
                gangMapPVo.userCount = (short)set.size();
            }
        }else{
            gangMapPVo = new GangMapPVo();
            gangMapPVo.baseID = baseID;
            gangMapPVo.userCount = 1;
            if(nextVo == null){
                gangMapPVo.mapID = (short)mapBaseVo.ID;
                gangMapPVo.bossBlood = 0;
                gangMapPVo.level = (byte) gangMapBaseVo.level;
            }else{
                gangMapPVo.mapID = (short)gangMapBaseVo.ID;
                gangMapPVo.bossBlood = gangMapBaseVo.bossBlood;
                gangMapPVo.level = (byte) gangMapBaseVo.level;
            }
            map.put(baseID,gangMapPVo);
            HashSet<Long> users = new HashSet<>();
            users.add(user.guid);
            mapModel.mapUser.put(baseID,users);
        }
        gangMapPVo.passTime = JkTools.getGameServerTime(null);
        gangMapPVo.passUserID = user.guid;
        gangMapPVo.passUserName = user.cacheUserVo.name;
        int[] dragonWish = mapModel.dragonWish;
        if(mapModel.userMapAward.containsKey(baseID)){
            HashMap<Long,GangMapAwardPVo> awardMap =  mapModel.userMapAward.get(baseID);
            int now = JkTools.getGameServerTime(null);
            for(Map.Entry<Long,GangMapAwardPVo> item : awardMap.entrySet()){
                GangMapAwardPVo gangMapAwardPVo = item.getValue();
                int time = now - gangMapAwardPVo.nextTime;
                if(nextVo != null){
                    if(time < 0){
                        gangMapAwardPVo.nextTime = now+(((nextVo.limit - gangMapBaseVo.limit)*dragonWish[1]/100+(-time/(gangMapBaseVo.coolTime*dragonWish[3]/100)+1)*gangMapBaseVo.count*dragonWish[2]/100)/nextVo.count*dragonWish[2]/100+1)*(nextVo.coolTime*dragonWish[3]/100);
                    }else{
                        if(gangMapBaseVo.limit < nextVo.limit){
                            gangMapAwardPVo.nextTime = now+((nextVo.limit - gangMapBaseVo.limit)*dragonWish[1]/100/nextVo.count*dragonWish[2]/100+1)*nextVo.coolTime*dragonWish[3]/100;
                        }
                    }
                }
            }
        }else if(gangMapBaseVo.limit > 0){
            int now = JkTools.getGameServerTime(null);
            HashMap<Long,GangMapAwardPVo> awardMap = new HashMap<>();
            for(GangUserVo userVo : gangVo.users.values()){
                GangMapAwardPVo gangMapAwardPVo = new GangMapAwardPVo();
                gangMapAwardPVo.baseID = baseID;
                gangMapAwardPVo.nextTime = now+gangMapBaseVo.limit*dragonWish[1]/100/gangMapBaseVo.count*dragonWish[2]/100*gangMapBaseVo.coolTime*dragonWish[3]/100;
                awardMap.put(userVo.cacheUserVo.guid,gangMapAwardPVo);
            }
            mapModel.userMapAward.put(baseID,awardMap);
        }
        MailPVo mailPVo = MailModel.createMail(10008,user.guid);
        ArrayList<AnnexPropPVo> list = new ArrayList<>();
        for(int i=0;i<gangMapBaseVo.killAward.length;i+=2){
            int id = gangMapBaseVo.killAward[i];
            if(id<0)continue;
            AnnexPropPVo pVo = new AnnexPropPVo();
            pVo.propID = id;
            pVo.count = gangMapBaseVo.killAward[i+1];
            list.add(pVo);
        }
        mailPVo.prop = list;
        user.mailModel.sendMail(mailPVo,true);
        MailPVo passMail = MailModel.createMail(100005,(long)0);
        ArrayList<AnnexPropPVo> props = new ArrayList<>();
        for(int i=0;i<gangMapBaseVo.killAward.length;i+=2){
            int id = gangMapBaseVo.killAward[i];
            if(id<0)continue;
            AnnexPropPVo pVo = new AnnexPropPVo();
            pVo.propID = id;
            pVo.count = gangMapBaseVo.killAward[i+1];
            props.add(pVo);
        }
        passMail.prop = props;
        LoopSendMailModel.addSystemMail(passMail,true);
        HashMap<Long,Integer> userMap = new HashMap<>();
        LoopSendMailModel.rankUsers.put(passMail.guid,userMap);
        for(long userID : mapModel.mapUser.get(baseID)){
            userMap.put(userID,0);
        }
        mapModel.mapUser.get(baseID).clear();
        MailModel.sendMailToOnLine(passMail);
        LoopSendMailModel.saveRankUser(passMail.guid);
        gangVo.saveGangMap();
    }

    public void clear(){
        userMapAward.clear();
        gangAllMap.clear();
        mapUser.clear();
        gangVo.saveGangMap();
        for(int i=1;i<dragonWish.length;i++){
            dragonWish[i] = 100;
        }
    }
}
