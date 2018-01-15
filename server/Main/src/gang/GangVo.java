package gang;

import comm.Client;
import comm.Model;
import gang.guess.GuessModel;
import gang.map.MapModel;
import gang.talk.GangTalkModel;
import gluffy.utils.JkTools;
import gluffy.utils.RankSortedList;
import protocol.*;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.GangDragonWishBaseVo;
import table.GangUserDataEnum;
import table.GangbuildBaseVo;
import table.UserDataEnum;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by admin on 2016/6/29.
 */
public class GangVo {
    public long gangID;
    public String gangName;
    public byte level;
    public int exp;
    public String notice;
    public Map<Long,GangUserVo> users = new HashMap<>();
    public GangUserVo master;
    public int rank;
    public int zdl;
    public int zdlLimit;
    public byte maxLevel = Model.gangMaxLevel;
    public int maxUserCount;
    public int maxLikes;
    public int lastResetDay;
    public byte portrait;
    public GangTalkModel gangTalkModel = new GangTalkModel();
    public Map<Byte, GangBuildPVo> gangBuildMap = new HashMap<>();
    public Map<Long,GangApplyPVo> gangApplyMap = new HashMap<>();
    public ArrayList<GangApplyPVo> gangApplyList = new ArrayList<>();
    public ArrayList<GangLogPVo> gangLogList = new ArrayList<>();
    public RankSortedList<RankGangUserVo> rankGangUserList = new RankSortedList<>();
    public HashMap<Integer,IntIntPVo> giftMap = new HashMap<>();
    public ArrayList<GangGiftLogPVo> giftLogList = new ArrayList<>();
    public GuessModel guessModel = new GuessModel();
    public MapModel mapModel = new MapModel(this);

    public void init(){
        for(ArrayList<GangbuildBaseVo> list : Model.GangbuildBaseMap.values()){
            GangbuildBaseVo vo = list.get(0);
            if(vo == null)continue;
            GangBuildPVo pVo = new GangBuildPVo();
            pVo.id = (byte)vo.ID;
            pVo.level = (byte) vo.level;
            pVo.effect = vo.effect[0];
            pVo.costData = vo.costData;
            pVo.discount = 100;
            gangBuildMap.put(pVo.id,pVo);
        }
        maxLikes = Model.GameSetBaseMap.get(9).intArray[0];
        mapModel.gangDragon.key = 1;
        saveGangMap();
        saveGangBuildInfo();
    }

    public void createGangInfoRspd(Client client){
        if(!users.containsKey(client.currentUser.guid))return;
        GangUserVo vo = users.get(client.currentUser.guid);
        new GangInfoRspd(client,gangID,rank,gangName,level,exp,notice,master.cacheUserVo.guid,master.cacheUserVo.guid,master.cacheUserVo.name,zdl,users.size(),maxUserCount,(byte) client.currentUser.getUserData(UserDataEnum.IsGetGangAward),vo.likeTime,vo.office, zdlLimit,portrait,vo.isGetGift,vo.luckValue);
    }

    public void createGangDetailRspd(Client client){
        new GangDetailRspd(client,gangID,rank,gangName,level,exp,notice,master.cacheUserVo.guid,master.cacheUserVo.guid,master.cacheUserVo.name,zdl,users.size(),maxUserCount,portrait);
    }

    public void createGangBuildInfoRspd(Client client){
        ArrayList<GangBuildPVo> list = new ArrayList<>();
        for(GangBuildPVo pVo : gangBuildMap.values()){
            list.add(pVo);
        }
        new GangBuildInfoRspd(client,list);
    }

    public void addLog(short type,String name){
        GangLogPVo pVo = new GangLogPVo();
        pVo.name = name;
        pVo.type = type;
        pVo.time = JkTools.getGameServerTime(null);
        if(gangLogList.size() >= Model.GameSetBaseMap.get(11).intArray[1]){
            gangLogList.remove(0);
        }
        gangLogList.add(pVo);
        saveGangLog();
    }

    public GangMemberListRspd createGangMemberRspd(Client client){
        ArrayList<GangUserPVo> proUsers = new ArrayList<>();
        for(GangUserVo user: users.values()){
            GangUserPVo pVo = new GangUserPVo();
            pVo.guid = user.cacheUserVo.guid;
            pVo.baseID = user.cacheUserVo.baseID;
            pVo.level = user.cacheUserVo.level;
            pVo.name = user.cacheUserVo.name;
            pVo.office = user.office;
            pVo.portrait = user.cacheUserVo.portrait;
            pVo.zdl = user.cacheUserVo.zdl;
            pVo.vip = user.cacheUserVo.passportVo.vip;
            Client client1 = Client.getOne(user.cacheUserVo.passportVo.guid);
            if(client1 != null && client1.currentUser != null && client1.currentUser.cacheUserVo != null){
                if(client1.currentUser.cacheUserVo.guid == user.cacheUserVo.guid){
//                    long time = System.currentTimeMillis() - client1.lastRqstTime;
//                    if(time>60000*5){
//                        pVo.time = (int)(client1.lastRqstTime/1000);
//                    }else{
//                        pVo.time = -1;
//                    }
                    //离线时间 等网关通知 找不帅
                    pVo.time = -1;
                }else{
                    pVo.time = user.cacheUserVo.lastLoginTime;
                }
            }else{
                pVo.time = user.cacheUserVo.lastLoginTime;
            }
            pVo.contribution = user.getGangUserData(GangUserDataEnum.CONTRIBUTION);
            pVo.contributionToday = user.contributionToday;
            pVo.userID = user.cacheUserVo.guid;
            proUsers.add(pVo);
        }
        return  new GangMemberListRspd(client,proUsers);
    }

    public void addExp(Client client,int value){
        exp += value;
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_EXP,exp);
        createGangInfoRspd(client);
    }

    public void initUserRankList(){
        ArrayList<GangUserVo> tempList=new ArrayList<>();
        tempList.addAll(users.values());
        Collections.sort(tempList,new Comparator<GangUserVo>() {
            @Override
            public int compare(GangUserVo o1, GangUserVo o2) {
                if(o1.office == o2.office){
                    return o2.cacheUserVo.zdl-o1.cacheUserVo.zdl;
                }else{
                    return o2.office - o1.office;
                }
            }
        });
        for (GangUserVo gangUserVo : tempList) {
            rankGangUserList.addEnd(new RankGangUserVo(gangUserVo));
        }
    }

    public void loadGangBuildInfo(String str){
        if (str == null || str.length() < 5){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        maxLikes = buffer.getInt();
        byte count = buffer.get();
        for(int i=0;i<count;i++){
            GangBuildPVo pVo = new GangBuildPVo();
            pVo.fromBytes(buffer);
            gangBuildMap.put(pVo.id,pVo);
        }
    }

    public void saveGangBuildInfo(){
        byte[] bytes = new byte[gangBuildMap.size()*15+5];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(maxLikes);
        buffer.put((byte) gangBuildMap.size());
        for(GangBuildPVo pVo : gangBuildMap.values()){
            pVo.toBytes(buffer);
        }
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_BUILD,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadGangApplyInfo(String str){
        if (str == null || str.length() < 1){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte count = buffer.get();
        for(byte i=0;i<count;i++){
            GangApplyPVo pVo = new GangApplyPVo();
            pVo.fromBytes(buffer);
            gangApplyList.add(pVo);
            gangApplyMap.put(pVo.userID,pVo);
        }
    }

    public void saveGangApplyInfo(){
        int size = gangApplyList.size()*(26+2);
        for(GangApplyPVo pVo : gangApplyList){
            size += JkTools.getStringLength(pVo.name);
        }
        byte[] bytes = new byte[size+1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) gangApplyList.size());
        for(GangApplyPVo pVo : gangApplyList){
            pVo.toBytes(buffer);
        }
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_APPLY_LIST,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadGangLog(String str){
        if (str == null || str.length() < 1){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte count = buffer.get();
        for(byte i=0;i<count;i++){
            GangLogPVo pVo = new GangLogPVo();
            pVo.fromBytes(buffer);
            gangLogList.add(pVo);
        }
    }

    public void saveGangLog(){
        int size = gangLogList.size()*(6+2);
        for(GangLogPVo pVo : gangLogList){
            size += JkTools.getStringLength(pVo.name);
        }
        byte[] bytes = new byte[size+1];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) gangLogList.size());
        for(GangLogPVo pVo : gangLogList){
            pVo.toBytes(buffer);
        }
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_GANG_LOG,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadGangGiftLog(String str){
        if (str == null || str.length() < 1){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short count = buffer.getShort();
        for(byte i=0;i<count;i++){
            GangGiftLogPVo pVo = new GangGiftLogPVo();
            pVo.fromBytes(buffer);
            giftLogList.add(pVo);
        }
    }

    public void saveGangGiftLog(){
        int size = giftLogList.size()*(8+2);
        for(GangGiftLogPVo pVo : giftLogList){
            size += JkTools.getStringLength(pVo.name);
        }
        byte[] bytes = new byte[size+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) giftLogList.size());
        for(GangGiftLogPVo pVo : giftLogList){
            pVo.toBytes(buffer);
        }
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_GIFT_LOG,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadGangGiftList(String str){
        if (str == null || str.length() < 1){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short count = buffer.getShort();
        for(byte i=0;i<count;i++){
            IntIntPVo pVo = new IntIntPVo();
            pVo.fromBytes(buffer);
            giftMap.put(pVo.key,pVo);
        }
    }

    public void saveGangGiftList(){
        byte[] bytes = new byte[giftMap.size()*8+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) giftMap.size());
        for(IntIntPVo pVo : giftMap.values()){
            pVo.toBytes(buffer);
        }
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_GIFT_LIST,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void saveGangMap(){
        int size = 6;
        for(HashSet<Long> set : mapModel.mapUser.values()){
            size += set.size()*8+2+2;
        }
        for(GangMapPVo gangMapPVo : mapModel.gangAllMap.values()){
            size += JkTools.getStringLength(gangMapPVo.passUserName)+2+23;
        }
        for(Map.Entry<Short,HashMap<Long,GangMapAwardPVo>> item: mapModel.userMapAward.entrySet()){
            size += item.getValue().size()*14+2+2;
        }
        byte[] bytes = new byte[size+5];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)mapModel.mapUser.size());
        for(Map.Entry<Short,HashSet<Long>> item : mapModel.mapUser.entrySet()){
            HashSet<Long> set = item.getValue();
            buffer.putShort((short) set.size());
            for(long userID : set){
                buffer.putLong(userID);
            }
            buffer.putShort(item.getKey());
        }
        buffer.putShort((short)mapModel.gangAllMap.size());
        for(GangMapPVo gangMapPVo : mapModel.gangAllMap.values()){
            gangMapPVo.toBytes(buffer);
        }
        buffer.putShort((short)mapModel.userMapAward.size());
        for(Map.Entry<Short,HashMap<Long,GangMapAwardPVo>> item: mapModel.userMapAward.entrySet()){
            HashMap<Long,GangMapAwardPVo> map = item.getValue();
            buffer.putShort((short) map.size());
            for(Map.Entry<Long,GangMapAwardPVo> entry : map.entrySet()){
                GangMapAwardPVo gangMapAwardPVo = entry.getValue();
                buffer.putShort(gangMapAwardPVo.baseID);
                buffer.putInt(gangMapAwardPVo.nextTime);
                buffer.putLong(entry.getKey());
            }
            buffer.putShort(item.getKey());
        }
        mapModel.gangDragon.toBytes(buffer);
        AllSql.gangSql.update(this,AllSql.gangSql.FIELD_GANG_MAP,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadGangMap(String str){
        if(str == null || str.length()<1)return;
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short mapUserSize = buffer.getShort();
        for(short i=0;i<mapUserSize;i++){
            HashSet<Long> set = new HashSet<>();
            short setSize = buffer.getShort();
            for(short j=0;j<setSize;j++){
                set.add(buffer.getLong());
            }
            mapModel.mapUser.put(buffer.getShort(),set);
        }
        short mapSize = buffer.getShort();
        for(short i=0;i<mapSize;i++){
            GangMapPVo gangMapPVo = new GangMapPVo();
            gangMapPVo.fromBytes(buffer);
            mapModel.gangAllMap.put(gangMapPVo.baseID,gangMapPVo);
        }
        short awardSize = buffer.getShort();
        for(short i=0;i<awardSize;i++){
            HashMap<Long,GangMapAwardPVo> awardMap = new HashMap<>();
            short awardMapSize = buffer.getShort();
            for(short j=0;j<awardMapSize;j++){
                GangMapAwardPVo gangMapAwardPVo = new GangMapAwardPVo();
                gangMapAwardPVo.baseID = buffer.getShort();
                gangMapAwardPVo.nextTime = buffer.getInt();
                awardMap.put(buffer.getLong(),gangMapAwardPVo);
            }
            mapModel.userMapAward.put(buffer.getShort(),awardMap);
        }
        mapModel.gangDragon.fromBytes(buffer);
        for(int i=0;i<Model.GangDragonWishBaseMap.size();i++){
            GangDragonWishBaseVo vo = Model.GangDragonWishBaseMap.get(i+1);
            if(vo.needLevel > mapModel.gangDragon.key)continue;
            mapModel.dragonWish[vo.ID] = vo.effct+100;
        }
    }
}
