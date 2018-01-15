package mine;

import comm.CacheUserVo;
import comm.Model;
import gameset.ActiveVo;
import gameset.GameSetModel;
import gang.Gang;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import mail.LoopSendMailModel;
import mail.MailModel;
import protocol.*;
import rank.RankModel;
import sqlCmd.AllSql;
import table.GameSetBaseVo;
import table.MineralResBaseVo;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by admin on 2017/7/4.
 */
public class MineModel extends BaseBlobDeal{
    public static final int RANK_COUNT = 100;
    public static ArrayList<MineRankPVo> lastMineList = new ArrayList<>();
    public static ArrayList<MineRankPVo> lastGangMineList = new ArrayList<>();
    public static HashMap<Long,Integer> lastUserMine = new HashMap<>();

    public byte type;
    public byte index;
    //公有矿
    public static ArrayList<MineRoomVo> allRooms = new ArrayList<>();
    public ArrayList<CacheUserVo> publicTeamMap = new ArrayList<>();
    public MineRoomVo myRoom;
    public int lastTime;

    //私有矿
    public int total;
    public ArrayList<PropPVo> stoneList = new ArrayList<>();
    public int lostMineCount;
    public int lostStoneCount;
    public int startTime;
    public int endTime;
    public byte mineType = 1;
    public HashMap<Byte, MineRecruitVo> teamMap = new HashMap<>();
    public HashSet<Long> used = new HashSet<>();

    public boolean sendLootMsg;
    public int protectTime;
    public int mineStartTime;
    public int mineEndTime;
    public int lastLootedTime;
    public HashSet<Long> lootset= new HashSet<>();
    public int lootTime;
    public int tempMineCount;
    public int tempStoneCount;
    public HashMap<Long,int[]> costMap = new HashMap<>();
    public HashMap<Byte, CacheUserVo> indexMap = new HashMap<>();

    public static void sendMineReward(){
        MineModel.lastUserMine.clear();
        MineModel.lastMineList.clear();
        MineModel.lastGangMineList.clear();

        //公会榜
        ArrayList<MineRankPVo> list = MineModel.lastGangMineList;
        for(GangVo gangVo : Gang.allGangList){
            int mineCount = 0;
            for(GangUserVo gangUserVo : gangVo.users.values()){
                if(gangUserVo.cacheUserVo.rankMineVo != null){
                    mineCount += gangUserVo.cacheUserVo.rankMineVo.mineCount;
                }
            }
            if(mineCount <= 0)continue;
            int index = 0;
            for(int i=0;i<list.size();i++){
                if(mineCount > list.get(i).rankScore){
                    index = i;
                    break;
                }
            }
            if(index == 0){
                index = list.size();
            }
            MineRankPVo mineRankPVo = new MineRankPVo();
            mineRankPVo.guid = gangVo.gangID;
            mineRankPVo.name = gangVo.gangName;
            mineRankPVo.rankScore = mineCount;
            list.add(index,mineRankPVo);
        }

        for(MineRankPVo mineRankPVo : list){
            MailPVo gangMail = MailModel.createMail(100007,(long)0);
            LoopSendMailModel.addSystemMail(gangMail,true);
            HashMap<Long,Integer> gangUserMap = new HashMap<>();
            LoopSendMailModel.rankUsers.put(gangMail.guid,gangUserMap);

            GangVo gangVo = Gang.allGangMap.get(mineRankPVo.guid);
            if(gangVo == null)continue;
            for(GangUserVo gangUserVo : gangVo.users.values()){
                gangUserMap.put(gangUserVo.cacheUserVo.guid,mineRankPVo.rankIndex);
            }

            MailModel.sendMailToOnLine(gangMail);
            LoopSendMailModel.saveRankUser(gangMail.guid);
        }

        //个人榜
        MailPVo rankMail = MailModel.createMail(100006,(long)0);
        LoopSendMailModel.addSystemMail(rankMail,true);
        HashMap<Long,Integer> userMap = new HashMap<>();
        LoopSendMailModel.rankUsers.put(rankMail.guid,userMap);
        HashSet<Long> gangSet = new HashSet<>();
        for(RankMineVo rankMineVo : RankModel.rankMineList.list){
            userMap.put(rankMineVo.cacheUserVo.guid,rankMineVo.orderIndex);
            rankMineVo.cacheUserVo.rankMineVo = null;
            MineRankPVo mineRankPVo = new MineRankPVo();
            mineRankPVo.guid = rankMineVo.cacheUserVo.guid;
            mineRankPVo.name = rankMineVo.cacheUserVo.name;
            mineRankPVo.rankIndex = rankMineVo.orderIndex;
            mineRankPVo.rankScore = rankMineVo.mineCount;
            GangVo gangVo = rankMineVo.cacheUserVo.gang.gangVo;
            if(gangVo != null){
                if(!gangSet.contains(gangVo.gangID)) {
                    gangSet.add(gangVo.gangID);
                    mineRankPVo.gangMax = 1;
                }
            }
            MineModel.lastMineList.add(mineRankPVo);
        }
        for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
            if(cacheUserVo.rankMineVo == null)continue;
            MineModel.lastUserMine.put(cacheUserVo.guid,cacheUserVo.rankMineVo.mineCount);
            cacheUserVo.rankMineVo = null;
        }
        RankModel.rankMineList.list.clear();
        MailModel.sendMailToOnLine(rankMail);
        LoopSendMailModel.saveRankUser(rankMail.guid);

        GameSetModel.gameSetVo.saveMine();
    }

    public void joinRoom(){
        MineRoomVo myRoom = null;
        for(MineRoomVo mineRoomVo : allRooms){
            if(mineRoomVo.roomUsers.size() < Model.GameSetBaseMap.get(75).intValue){
                myRoom = mineRoomVo;
                break;
            }
        }
        if(myRoom == null){
            myRoom = new MineRoomVo();
            allRooms.add(myRoom);
        }
        myRoom.roomUsers.put(user.guid,user.cacheUserVo);
        this.myRoom = myRoom;
    }

    public static void flushPublicMine(MineRoomVo roomVo) {
        int[] timeArr = Model.GameSetBaseMap.get(64).intArray;
        int[] countArr = Model.GameSetBaseMap.get(63).intArray;
        int[] initCountArr = Model.GameSetBaseMap.get(75).intArray;
        int now = JkTools.getGameServerTime(null);
        if (roomVo.lastCommTime == 0) {
            ActiveVo activeVo = GameSetModel.timeMap.get(17);
            if (activeVo == null) return;
            int startTime = activeVo.startTime;
            int time = now - startTime;
//            roomVo.commCount = Math.min(time / timeArr[0] + time / timeArr[1] * (10 - 1), countArr[0]);
//            roomVo.highCount = Math.min(time / timeArr[2] + time / timeArr[3] * (10 - 1), countArr[1]);
//            roomVo.rareCount = Math.min(time / timeArr[4] + time / timeArr[5] * (5 - 1), countArr[2]);
            roomVo.commCount = Math.min(initCountArr[0],countArr[0]);
            roomVo.highCount = Math.min(initCountArr[1],countArr[1]);
            roomVo.rareCount = Math.min(initCountArr[2],countArr[2]);
            addPublicMine((byte) 101, roomVo.commCount, roomVo);
            addPublicMine((byte) 102, roomVo.highCount, roomVo);
            addPublicMine((byte) 103, roomVo.rareCount, roomVo);
            roomVo.lastCommTime = now - time % timeArr[0];
            roomVo.lastMoreCommTime = now - time % timeArr[1];
            roomVo.lastHighTime = now - time % timeArr[2];
            roomVo.lastMoreHighTime = now - time % timeArr[3];
            roomVo.lastRareTime = now - time % timeArr[4];
            roomVo.lastMoreRareTime = now - time % timeArr[5];
        } else {
            if(roomVo.commCount < countArr[0]){
                int commTime = now - roomVo.lastCommTime;
                int moreCommTime = now - roomVo.lastMoreCommTime;
                int commCount =  Math.min(commTime/timeArr[0] + moreCommTime/timeArr[1]*(10 - 1),countArr[0]-roomVo.commCount);
                addPublicMine((byte) 101, commCount, roomVo);
                roomVo.lastCommTime = now - commTime % timeArr[0];
                roomVo.lastMoreCommTime = now - moreCommTime % timeArr[1];
                roomVo.commCount += commCount;
            }
            if(roomVo.highCount < countArr[1]){
                int highTime = now - roomVo.lastHighTime;
                int moreHighTime = now - roomVo.lastMoreHighTime;
                int highCount = Math.min(highTime/timeArr[2] + moreHighTime/timeArr[3]*(10 - 1),countArr[1]-roomVo.highCount);
                addPublicMine((byte) 102, highCount, roomVo);
                roomVo.lastHighTime = now - highTime % timeArr[2];
                roomVo.lastMoreHighTime = now - moreHighTime % timeArr[3];
                roomVo.highCount += highCount;
            }
            if(roomVo.rareCount < countArr[2]){
                int rareTime = now - roomVo.lastRareTime;
                int moreRareTime = now - roomVo.lastMoreRareTime;
                int rareCount = Math.min(rareTime/timeArr[4] + moreRareTime/timeArr[5]*(5 - 1),countArr[2]-roomVo.rareCount);
                addPublicMine((byte) 103, rareCount, roomVo);
                roomVo.lastRareTime = now - rareTime % timeArr[4];
                roomVo.lastMoreRareTime = now - moreRareTime % timeArr[5];
                roomVo.rareCount += rareCount;
            }
            roomVo.commCount = 0;
            roomVo.highCount = 0;
            roomVo.rareCount = 0;
            Iterator<Map.Entry<Byte,PublicMinePVo>> it = roomVo.publicMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<Byte,PublicMinePVo> entry = it.next();
                PublicMinePVo publicMinePVo = entry.getValue();
                if(publicMinePVo.lootTime != 0 && now > publicMinePVo.lootTime){
                    System.out.println(publicMinePVo.mineType);
                    it.remove();
                }else{
                    if(publicMinePVo.mineType == 101)roomVo.commCount++;
                    if(publicMinePVo.mineType == 102)roomVo.highCount++;
                    if(publicMinePVo.mineType == 103)roomVo.rareCount++;
                }
            }
        }
    }

    private static void addPublicMine(byte mineType,int num,MineRoomVo roomVo){
        for(int i=0;i<num;i++){
            byte temp = 0;
            for(byte index=temp;index<1000;index++){
                if(roomVo.publicMap.containsKey(index))continue;
                PublicMinePVo publicMinePVo = new PublicMinePVo();
                publicMinePVo.index = index;
                publicMinePVo.mineType = mineType;
                roomVo.publicMap.put(index,publicMinePVo);
                temp = (byte)(index+ 1);
                break;
            }
        }
    }

    public static void calculate(int now,MineModel mineModel,boolean isLoot){
        GameSetBaseVo vo  = Model.GameSetBaseMap.get(57);
        MineralResBaseVo mineralResBaseVo = Model.MineralResBaseMap.get((int)mineModel.mineType);
        int avgMine = mineModel.total/(mineralResBaseVo.operateTime/vo.intValue);
        int time = now - mineModel.startTime;
        int index = time%(vo.intValue*60)/Model.GameSetBaseMap.get(58).intValue;
        int percent = 0;
        for(int i=0;i<index;i++){
            percent += vo.intArray[i];
        }
        int nowMine = time/(vo.intValue*60)*avgMine+avgMine*percent/100;
        if(isLoot){
            mineModel.tempMineCount = (nowMine-mineModel.lostMineCount)/2;
        }else{
            mineModel.tempMineCount = nowMine;
        }

        if(mineModel.stoneList.size() != 0){
            int avgStoneTime = mineralResBaseVo.operateTime*60/mineModel.stoneList.size();
            int nowStoneCount = time/avgStoneTime;
            if(isLoot){
                mineModel.tempStoneCount = (nowStoneCount - mineModel.lostStoneCount)/2;
            }else{
                mineModel.tempMineCount = nowStoneCount;
            }
        }
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[stoneList.size()*8+23+used.size()*8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(total);
        buffer.put((byte) stoneList.size());
        for(PropPVo propPVo : stoneList){
            buffer.putInt(propPVo.baseID);
            buffer.putInt(propPVo.count);
        }
        buffer.putInt(mineStartTime);
        buffer.putInt(mineEndTime);
        buffer.putInt(startTime);
        buffer.putInt(endTime);
        buffer.put(mineType);
        buffer.put((byte)used.size());
        for(long id : used){
            buffer.putLong(id);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        total = buffer.getInt();
        byte count = buffer.get();
        for(int i=0;i<count;i++){
            PropPVo propPVo = new PropPVo();
            propPVo.baseID = buffer.getInt();
            propPVo.count = buffer.getInt();
            stoneList.add(propPVo);
        }
        mineStartTime = buffer.getInt();
        mineEndTime = buffer.getInt();
        startTime = buffer.getInt();
        endTime = buffer.getInt();
        mineType = buffer.get();
        byte usedCount = buffer.get();
        for(int i=0;i<usedCount;i++){
            used.add(buffer.getLong());
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_MINE,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
