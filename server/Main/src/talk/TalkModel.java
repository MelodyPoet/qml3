package talk;

import comm.CacheUserVo;
import comm.Model;
import comm.RankListItem;
import comm.User;
import gluffy.utils.JkTools;
 import gluffy.utils.RankSortedList;
import protocol.TalkMsgPVo;
import protocol.TalkRedPacketUserPVo;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;
import utils.UserVoAdapter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by jackie on 14-5-2.
 */
public class TalkModel {
    public static final int LIMIT_COUNT = 500;
    public static ArrayList<TalkMsgPVo> talkMsgList = new ArrayList<>();
    public static Map<Long,TalkMsgPVo> talkMsgMap = new HashMap<>();
    public static Map<Long,TalkRedPacketVo> redPacketMap = new HashMap<>();
    public static RankSortedList<TalkLikeRankItem> rankLikeList = new RankSortedList<>();
    public static HashMap<Long, TalkLikeRankItem> rankLikeMap = new HashMap<>();
    public static HashMap<Long, HashSet> userLikesMap = new HashMap<>();
    public static int systemMsgNum = 0;
    public static int redPackMsgNum = 0;
    public static int bossMsgNum = 0;
    public static final int BOSS_LIMIT_LIFE = 60 * 60 / 2;//BOSS消息保存半小时
    public static final int COMMOM_LIMIT_LIFE = 60 * 60 * 2;//普通消息保存2小时
    public static int lastOperateDay = JkTools.getCalendar().get(Calendar.DAY_OF_YEAR);


    public static void makeLike(long tempID, User user) {
        TalkLikeRankItem vo = null;
        HashSet<Long> userLikes = new HashSet<>();
        if (rankLikeMap.containsKey(tempID)) {
            vo = rankLikeMap.get(tempID);
            userLikes = userLikesMap.get(vo.vo.ID);
            if (userLikes == null) {
                return;
            }
            if (userLikes.contains(user.guid)) {
                return;
            }
            vo.vo.likes++;
            rankLikeList.sortItem(vo, vo.orderScore());
        } else {
            vo = new TalkLikeRankItem();
            vo.vo = talkMsgMap.get(tempID);
            vo.vo.likes = 1;
            rankLikeList.addEnd(vo);
            rankLikeMap.put(vo.vo.ID,vo);
        }
        userLikes.add(user.guid);
        userLikesMap.put(vo.vo.ID, userLikes);
        AllSql.worldTreeSql.update(vo.vo,AllSql.worldTreeSql.FIELD_LIKES,vo.vo.likes);
        saveUserLikes(vo.vo,userLikes);
    }

    public static void addTalkMsgInTimeList(User user, String msg, byte classType, byte type, byte redpacketID) {
        TalkRedPacketVo packetVo = null;

        if (type == TalkEnum.REDPACKET) {

            int total = Model.RedPacketsBaseMap.get(1).total[redpacketID];
            if (user.costUserDataAndProp(UserDataEnum.DIAMOND, total, true, ReasonTypeEnum.RED_PACKET) == false) return;
            int count = Model.RedPacketsBaseMap.get(1).count[redpacketID];
            packetVo = new TalkRedPacketVo();
            packetVo.totalDiamond = total;
            packetVo.totalCount = count;
            float piece = (float) total / count;
            int min = Math.max(1, (int) (piece * 0.2));
            for (int i = 0; i < count; i++) {
                packetVo.diamondList.add(min);

            }
            total -= min * count;
            for (int i = 0; i < count; i++) {
                int val = Math.max(1, (int) (Math.random() * piece * 1.85));
                packetVo.diamondList.set(i, packetVo.diamondList.get(i) + val);
                total -= val;
                if (total <= 0){
                    packetVo.diamondList.set(i, packetVo.diamondList.get(i) + total);
                    break;
                }
            }
            if(total > 0){
                for (int i = 0; i < count; i++) {
                    if(packetVo.diamondList.get(i) > piece)continue;
                    int val = Math.max(1,(int)(Math.random() * piece * 1));
                    packetVo.diamondList.set(i, packetVo.diamondList.get(i) + val);
                    total -= val;
                    if (total <= 0){
                        packetVo.diamondList.set(i, packetVo.diamondList.get(i) + total);
                        break;
                    }
                }
            }
            //packetVo.onepiece = total / count;
            user.addUserData(UserDataEnum.LJ_REDPACKET_VALUE,total,true);
        }

        TalkMsgPVo vo = new TalkMsgPVo();
        vo.roleName = user.cacheUserVo.name;
        vo.roleID = user.guid;
        vo.msg = msg;
        vo.classType = classType;
        vo.type = type;
        vo.portrait = user.cacheUserVo.portrait;
        vo.createTime = JkTools.getGameServerTime(null);
        vo.level = user.cacheUserVo.level;
        vo.yunvaID = user.cacheUserVo.yunwaID;
        if(vo.classType == TalkEnum.GANG){
            vo.gangID = user.cacheUserVo.gang.gangVo==null ?  0 : user.cacheUserVo.gang.gangVo.gangID;
        }
        addTalkMsg(vo,packetVo,true);
        user.addUserData(UserDataEnum.LJ_TALK_COUNT,1,true);
    }

    public static void addTalkMsg(TalkMsgPVo pVo,TalkRedPacketVo packetVo,boolean insertNew){
        if(insertNew){
            AllSql.worldTreeSql.insertNew(pVo);
        }

        if(talkMsgList.size() >= LIMIT_COUNT){
            TalkMsgPVo talkMsgPVo = talkMsgList.get(0);
            removeTalkMsg(talkMsgPVo);
        }
        talkMsgList.add(pVo);
        talkMsgMap.put(pVo.ID,pVo);

        if (pVo.type == TalkEnum.REDPACKET) {
            redPackMsgNum++;
        }else if (pVo.classType == TalkEnum.OFFICIAL) {
            if (pVo.type == TalkEnum.BOSS) {
                bossMsgNum++;
            } else {
                systemMsgNum++;
            }
        }
        if (packetVo != null) {
            redPacketMap.put(pVo.ID,packetVo);
            saveRedPacket(pVo,packetVo);
        }
        for(ShieldBaseVo vo: Model.ShieldBaseMap.values()){
            if(pVo.msg.indexOf(vo.name) != -1){
                StringBuffer sb = new StringBuffer();
                for(int i=0;i<vo.name.length();i++){
                    sb.append("*");
                }
                pVo.msg = pVo.msg.replace(vo.name,sb.toString());
            }
        }
    }

    public static void addTalkMsgBySystem(int id) {
        TalkRedPacketVo packetVo = null;
        WorldTreeBaseVo vo = Model.WorldTreeBaseMap.get(id);
        if (vo.type == TalkEnum.REDPACKET) {

            int total = Model.RedPacketsBaseMap.get(1).total[vo.redpacketID];
            int count = Model.RedPacketsBaseMap.get(1).count[vo.redpacketID];
            packetVo = new TalkRedPacketVo();
            packetVo.totalDiamond = total;
            packetVo.totalCount = count;
            float piece = (float) total / count;
            int min = Math.max(1, (int) (piece * 0.2));
            for (int i = 0; i < count; i++) {
                packetVo.diamondList.add(min);

            }
            total -= min * count;
            for (int i = 0; i < count; i++) {
                int val = Math.max(1, (int) (Math.random() * piece * 1.85));
                packetVo.diamondList.set(i, packetVo.diamondList.get(i) + val);
                total -= val;
                if (total <= 0){
                    packetVo.diamondList.set(i, packetVo.diamondList.get(i) + total);
                    break;
                }
            }
            if(total > 0){
                for (int i = 0; i < count; i++) {
                    if(packetVo.diamondList.get(i) > piece)continue;
                    int val = Math.max(1,(int)(Math.random() * piece * 1));
                    packetVo.diamondList.set(i, packetVo.diamondList.get(i) + val);
                    total -= val;
                    if (total <= 0){
                        packetVo.diamondList.set(i, packetVo.diamondList.get(i) + total);
                        break;
                    }
                }
            }
            //packetVo.onepiece = total / count;
        }
        TalkMsgPVo pVo = new TalkMsgPVo();
        pVo.roleName = vo.senderName;
        pVo.roleID = id;
        pVo.portrait = 0;
        pVo.msg = vo.msg;
        pVo.classType = (byte)vo.classType;
        pVo.type = (byte)vo.type;
        pVo.createTime = JkTools.getGameServerTime(null);
        LoopSendTalkModel.addSystemTalk(pVo,packetVo,true);
    }

    public static void loadUserLikes(Long id,String str){
        HashSet users = new HashSet<>();
        if (str == null || str.length() < 2){
            TalkModel.userLikesMap.put(id,users);
            return;
        }

        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int count = buffer.getShort();
        for(int i=0;i<count;i++){
            users.add(buffer.getLong());
        }
        TalkModel.userLikesMap.put(id,users);
    }

    public static void resetNewMsgItem(User user,Long lastTalkId){
        UserNewMsgItem newMsgItem = user.userNewMsgItem;
        if(newMsgItem.lastOperateDay != TalkModel.lastOperateDay || newMsgItem.lastReadRedPackMsg == 0) {
            newMsgItem.newRedPackMsgNum = TalkModel.redPackMsgNum;
            newMsgItem.lastReadRedPackMsg = lastTalkId;
            newMsgItem.isChange = true;
        }else{
            if(newMsgItem.lastReadRedPackMsg != lastTalkId){
                for (long i = newMsgItem.lastReadRedPackMsg + 1; i <= lastTalkId; i++) {
                    TalkMsgPVo pVo = TalkModel.talkMsgMap.get(i);
                    if (pVo == null) continue;
                    if (pVo.type == TalkEnum.REDPACKET) {
                        newMsgItem.newRedPackMsgNum++;
                    }
                }
                newMsgItem.lastReadRedPackMsg = lastTalkId;
                newMsgItem.isChange = true;
            }
        }
        if(newMsgItem.lastOperateDay != TalkModel.lastOperateDay || newMsgItem.lastReadBossMsg == 0) {
            newMsgItem.newBossMsgNum = TalkModel.bossMsgNum;
            newMsgItem.lastReadBossMsg = lastTalkId;
            newMsgItem.isChange = true;
        }else{
            if(newMsgItem.lastReadBossMsg != lastTalkId){
                for (long i = newMsgItem.lastReadBossMsg + 1; i <= lastTalkId; i++) {
                    TalkMsgPVo pVo = TalkModel.talkMsgMap.get(i);
                    if (pVo == null) continue;
                    if (pVo.type == TalkEnum.BOSS) {
                        newMsgItem.newBossMsgNum++;
                    }
                }
                newMsgItem.lastReadBossMsg = lastTalkId;
                newMsgItem.isChange = true;
            }
        }
        if(newMsgItem.lastOperateDay != TalkModel.lastOperateDay || newMsgItem.lastReadSystemMsg == 0) {
            newMsgItem.newSystemMsgNum = TalkModel.systemMsgNum;
            newMsgItem.lastReadSystemMsg = lastTalkId;
            newMsgItem.isChange = true;
        }else {
            if (newMsgItem.lastReadSystemMsg != lastTalkId) {
                for (long i = newMsgItem.lastReadSystemMsg + 1; i <= lastTalkId; i++) {
                    TalkMsgPVo pVo = TalkModel.talkMsgMap.get(i);
                    if (pVo == null) continue;
                    if (pVo.classType == TalkEnum.OFFICIAL&&pVo.type != TalkEnum.REDPACKET&&pVo.type != TalkEnum.BOSS) {
                        newMsgItem.newSystemMsgNum++;
                    }
                }
                newMsgItem.lastReadSystemMsg = lastTalkId;
                newMsgItem.isChange = true;
            }
        }

            if(newMsgItem.isChange == true){
                if(newMsgItem.lastOperateDay != TalkModel.lastOperateDay){
                    newMsgItem.lastOperateDay = TalkModel.lastOperateDay;
                }
                TalkModel.saveUserNewMsg(user);
                newMsgItem.isChange = false;
            }
    }

    public static void saveUserLikes(TalkMsgPVo talkMsgPVo,HashSet<Long> users){
        byte[] bytes = new byte[users.size()*8 + 2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)users.size());
        for(Long guid : users){
            buffer.putLong(guid);
        }
        AllSql.worldTreeSql.update(talkMsgPVo,AllSql.worldTreeSql.FIELD_USER_LIKES,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public static void loadUserNewMsg(User user,String str){
        if(TalkModel.talkMsgList.size() == 0)return;
        long lastTalkId = TalkModel.talkMsgList.get(TalkModel.talkMsgList.size()-1).ID;
        if (str == null || str.length() < 44){
            resetNewMsgItem(user,lastTalkId);
            return;
        }

        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        user.userNewMsgItem.lastOperateDay = buffer.getLong();
        user.userNewMsgItem.lastReadSystemMsg = buffer.getLong();
        user.userNewMsgItem.lastReadRedPackMsg = buffer.getLong();
        user.userNewMsgItem.lastReadBossMsg = buffer.getLong();
        user.userNewMsgItem.newRedPackMsgNum = buffer.getInt();
        user.userNewMsgItem.newBossMsgNum = buffer.getInt();
        user.userNewMsgItem.newSystemMsgNum = buffer.getInt();
        resetNewMsgItem(user,lastTalkId);
    }

    public static void saveUserNewMsg(User user){
//        System.out.println("====SAVE====newSystemMsgNum : "+user.userNewMsgItem.newSystemMsgNum+"=====newBossMsgNum"+user.userNewMsgItem.newBossMsgNum+"======newRedPackMsgNum"+user.userNewMsgItem.newRedPackMsgNum+"========");
        byte[] bytes = new byte[44];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(user.userNewMsgItem.lastOperateDay);
        buffer.putLong(user.userNewMsgItem.lastReadSystemMsg);
        buffer.putLong(user.userNewMsgItem.lastReadRedPackMsg);
        buffer.putLong(user.userNewMsgItem.lastReadBossMsg);
        buffer.putInt(user.userNewMsgItem.newRedPackMsgNum);
        buffer.putInt(user.userNewMsgItem.newBossMsgNum);
        buffer.putInt(user.userNewMsgItem.newSystemMsgNum);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_NEW_MSG_ITEM,"'"+new BASE64Encoder().encode(bytes)+"'");
    }


    public static void loadRedPacket(Long id,String str){
        if (str == null || str.length() < 14){
            return;
        }

        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        TalkRedPacketVo packetVo = new TalkRedPacketVo();
        int diamondListSize = buffer.getShort();
        for(int i=0;i<diamondListSize;i++){
            packetVo.diamondList.add(buffer.getInt());
        }
        int usersCount = buffer.getShort();
        for(int i=0;i<usersCount;i++){
            Long userID = buffer.getLong();
            int val = buffer.getInt();
            if(CacheUserVo.allMap.containsKey(userID)){
                CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
                TalkRedPacketUserPVo redpacketUserPVo = UserVoAdapter.toTalkRedPacketUserPVo(cacheUserVo, val);
                packetVo.users.add(redpacketUserPVo);
            }
        }
        packetVo.totalDiamond = buffer.getInt();
        packetVo.totalCount = buffer.getInt();
        int allGetSize = buffer.getShort();
        for(int i=0;i<allGetSize;i++){
            packetVo.allGet.put(buffer.getLong(),buffer.getInt());
        }
        TalkModel.redPacketMap.put(id,packetVo);
    }

    public static void saveRedPacket(TalkMsgPVo talkMsgPVo,TalkRedPacketVo packetVo){
        byte[] bytes = new byte[packetVo.diamondList.size()*4 + packetVo.users.size() * 12 + packetVo.allGet.size()* 12 + 6 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)packetVo.diamondList.size());
        for(Integer integer : packetVo.diamondList){
            buffer.putInt(integer);
        }
        buffer.putShort((short)packetVo.users.size());
        for(TalkRedPacketUserPVo userPVo : packetVo.users){
            buffer.putLong(userPVo.userID);
            buffer.putInt(userPVo.diamond);
        }
        buffer.putInt(packetVo.totalDiamond);
        buffer.putInt(packetVo.totalCount);
        buffer.putShort((short)packetVo.allGet.size());
        for(Long key : packetVo.allGet.keySet()){
            buffer.putLong(key);
            buffer.putInt(packetVo.allGet.get(key));
        }
        AllSql.worldTreeSql.update(talkMsgPVo,AllSql.worldTreeSql.FIELD_RED_PACKET,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public static boolean checkIsDead(TalkMsgPVo pVo){
        int time = JkTools.getGameServerTime(null) - pVo.createTime;
        if(pVo.classType == TalkEnum.COMMON){
            if(time > COMMOM_LIMIT_LIFE){
                removeTalkMsg(pVo);
                return true;
            }
        }
        if(pVo.classType == TalkEnum.OFFICIAL && pVo.type ==TalkEnum.BOSS){
            if(time > BOSS_LIMIT_LIFE){
                removeTalkMsg(pVo);
                return true;
            }
        }
        return false;
    }

    private static void removeTalkMsg(TalkMsgPVo pVo){
        if (pVo.type == TalkEnum.REDPACKET) {
            redPackMsgNum--;
        }else if (pVo.classType == TalkEnum.OFFICIAL) {
            if (pVo.type == TalkEnum.BOSS) {
                bossMsgNum--;
            } else {
                systemMsgNum--;
            }
        }
        talkMsgList.remove(pVo);
        talkMsgMap.remove(pVo.ID);
        redPacketMap.remove(pVo.ID);
        if(rankLikeMap.containsKey(pVo.ID)){
            rankLikeList.list.remove(rankLikeMap.get(pVo.ID));
            rankLikeMap.remove(pVo.ID);
        }
        userLikesMap.remove(pVo.ID);
        AllSql.worldTreeSql.update(pVo,AllSql.worldTreeSql.FIELD_FLAG,1);
    }
}

class TalkLikeRankItem extends RankListItem {
    public TalkMsgPVo vo;

    @Override
    public int orderScore() {
        return (int) vo.likes;
    }
}


