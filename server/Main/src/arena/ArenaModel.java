package arena;

import airing.AiringModel;
import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import heroTag.HeroTagModel;
import mail.LoopSendMailModel;
import mail.MailModel;
import protocol.*;
import rank.RankModel;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by jackie on 14-5-2.
 */
public class ArenaModel extends BaseBlobDeal {

    public static int needLvl=10;
    public  static final   int FightTime=180;
    public static ArrayList<CacheArenaFighting> CacheArenaFightingList=new ArrayList<>();

    public  CacheArenaFighting cacheArenaFighting;
     public RankUserArenaVo fightingTarget;
    public int nextFightTime;
    public int bestRecord;

    public ArenaModel(User user) {
        this.user=  user;
    }


     @Override
    protected byte[] saveDataBytes() {
         byte[] bytes = new byte[4];
         ByteBuffer buffer = ByteBuffer.wrap(bytes);
         buffer.putInt(bestRecord);
         return bytes;
      //  if(user.cacheUserVo.arenaScore<=0)return null;
//        byte[] bytes=new byte[1+4+8+4];
//        ByteBuffer buffer=ByteBuffer.wrap(bytes);
//        buffer.put(canFightCount);
//        buffer.putInt(0);
//        buffer.putLong(0);
//        buffer.putInt(user.cacheUserVo.arenaScore);
//        return bytes;

    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {

        if(buffer==null)return;
        bestRecord = buffer.getInt();
//        canFightCount=buffer.get();
//        buffer.getInt();
//        buffer.getLong();
//
//        user.cacheUserVo.arenaScore=buffer.getInt();
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user, AllSql.userSql.FIELD_ARENA, saveData());

    }

    @Override
    public void unloadUser() {
        user=null;
    }

    public static ArrayList<Integer> getTargets(int selfIndex){
        ArrayList<Integer> targets=new ArrayList<>();
        if(selfIndex>=20000){
            targets.add(JkTools.getRandBetween(19880, 19900));
            targets.add(JkTools.getRandBetween(19900,19920));
            targets.add(JkTools.getRandBetween(19920,19940));
            targets.add(JkTools.getRandBetween(19940,19960));
            targets.add(JkTools.getRandBetween(19960,19980));
            targets.add(JkTools.getRandBetween(19980,20000));
        }else  if(selfIndex>=17000){
            targets.add(JkTools.getRandBetween(15000,16000,selfIndex));
            targets.add(JkTools.getRandBetween(16000,17000,selfIndex));
            targets.add(JkTools.getRandBetween(17000,18000,selfIndex));
            targets.add(JkTools.getRandBetween(19700,19800,selfIndex));
            targets.add(JkTools.getRandBetween(19800,19900,selfIndex));
            targets.add(JkTools.getRandBetween(19900,20000,selfIndex));
        }else   if(selfIndex>100){

            targets.add(getTarget(selfIndex,-0.42f,-0.01f,-3));
            targets.add(getTarget(selfIndex,-0.35f,-0.01f,-3));
            targets.add(getTarget(selfIndex,-0.25f,-0.01f,-3));
            targets.add(getTarget(selfIndex,-0.02f,-0.01f,-3));

            targets.add(getTarget(selfIndex, 0.06f, 0.02f, 4));
            targets.add(getTarget(selfIndex, 0.15f, 0.02f, 4));

        }else if(selfIndex>20){
            targets.add(getTarget(selfIndex,-0.6f,0,-3));
            targets.add(getTarget(selfIndex,-0.45f,0,-3));
            targets.add(getTarget(selfIndex,-0.3f,0,-3));
            targets.add(getTarget(selfIndex,-0.15f,0,-3));
            targets.add(getTarget(selfIndex,0.2f,0,3));
            targets.add(getTarget(selfIndex,0.35f,0,3));
        }else  if(selfIndex>10){
            int start= getTarget(selfIndex,-0.15f,0,2);
            targets.add(start-5-  JkTools.getRandBetween(0,1));
            targets.add(start-3-  JkTools.getRandBetween(0,1));
            targets.add(start-1-  JkTools.getRandBetween(0,1));
            targets.add(start);
            targets.add(getTarget(selfIndex,0.5f,0,3));
            targets.add(getTarget(selfIndex,0.9f,0,3));

        }
        else   {
            for (int i = 4; i >0; i--) {
                if(selfIndex-i>0)
                    targets.add(selfIndex-i);
            }
            if(targets.size()<6)
                targets.add(selfIndex+ JkTools.getRandBetween(1,3));
            if(targets.size()<6)
                targets.add(selfIndex+ JkTools.getRandBetween(3,7));
            if(targets.size()<6)
                targets.add(selfIndex+ JkTools.getRandBetween(7,12));
            if(targets.size()<6)
                targets.add(selfIndex+ JkTools.getRandBetween(12,18));
        }

        return targets;

    }

    public static  int   getTarget(int selfIndex,float param1, float rnd,float rndAdd){
        selfIndex+=selfIndex*param1;
        selfIndex= (int)(selfIndex+Math.random()*(selfIndex*rnd+rndAdd));
        return selfIndex;
    }

    public static void loopCompleteFight() {
        if(CacheArenaFightingList.size()==0)return;
        CacheArenaFighting caf= CacheArenaFightingList.get(0);
        if(caf.completed==true){
            caf.timeout=true;
            CacheArenaFightingList.remove(0);
            return;
        }
        if(caf.completedTime<System.currentTimeMillis()){
            CacheArenaFightingList.remove(0);
            caf.timeout=true;
            ArenaRecordPVo recordPVo = new ArenaRecordPVo();
            recordPVo.tempID1 = caf.user1.guid;
            recordPVo.name1 = caf.user1.name;
            recordPVo.level1 = caf.user1.level;
            recordPVo.zdl1 =caf.user1.zdl;
            recordPVo.tempID2 =caf.user2.guid;
            recordPVo.name2 = caf.user2.name;
            recordPVo.level2 = caf.user2.level;
            recordPVo.zdl2 = caf.user2.zdl;
            recordPVo.time = JkTools.getGameServerTime(null);

            caf.user1.rankArenaVo.addArenaRecord(recordPVo);
            caf.user2.rankArenaVo.addArenaRecord(recordPVo);
            recordPVo.isWin = false;
        }

    }

    public static void recordArenaReward(){
        GameSetBaseVo gameSetBaseVo = Model.GameSetBaseMap.get(70);
        MailPVo mailPVo = MailModel.createMail(100001,(long)0);
        LoopSendMailModel.addSystemMail(mailPVo,true);
        HashMap<Long,Integer> arenaRewardMap = new HashMap<>();
        int limit = Model.GameSetBaseMap.get(79).intValue;
        for (RankUserArenaVo arenaVo: RankModel.rankArenaList.list){
            if(arenaVo.orderIndex >= limit)break;
            if(arenaVo.orderIndex < gameSetBaseVo.intValue){
                HeroTagModel.addHeroTag(gameSetBaseVo.intArray,arenaVo.orderIndex+1,arenaVo.cacheUserVo);
            }
            if(arenaVo.orderIndex <20)airing(arenaVo);
            arenaRewardMap.put(arenaVo.cacheUserVo.guid,arenaVo.orderIndex);
        }
        LoopSendMailModel.rankUsers.put(mailPVo.guid,arenaRewardMap);
        MailModel.sendMailToOnLine(mailPVo);
        LoopSendMailModel.saveRankUser(mailPVo.guid);
    }


    public static LinkedList<ArenaRecordPVo> loadArenaRecord(String str){
        if (str == null || str.length() < 2){
            return null;
        }

        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        LinkedList<ArenaRecordPVo> recordList = new LinkedList<>();
        int count = buffer.getShort();
        int name1Size,name2Size;
        for(int i=0;i<count;i++){
            ArenaRecordPVo pVo = new ArenaRecordPVo();
            pVo.fromBytes(buffer);
//            pVo.tempID1 = buffer.getLong();
//            name1Size = buffer.getShort();
//            byte[] bb1 = new byte[name1Size];
//            buffer.get(bb1,0,name1Size);
//            ByteBuffer name1 = ByteBuffer.wrap(bb1);
//            pVo.name1 = JkTools.readString(name1);
//            pVo.level1 = buffer.get();
//            pVo.zdl1 = buffer.getInt();
//
//
//            pVo.tempID2 = buffer.getLong();
//            name2Size = buffer.getShort();
//            byte[] bb2 = new byte[name2Size];
//            buffer.get(bb2,0,name2Size);
//            ByteBuffer name2 = ByteBuffer.wrap(bb2);
//            pVo.name2 = JkTools.readString(name2);
//            pVo.level2 = buffer.get();
//            pVo.zdl2 = buffer.getInt();
//
//            pVo.time = buffer.getInt();
//            pVo.isWin = buffer.get() == (byte)0?false:true;
        }
        return recordList;
    }

    public static void saveArenaRecord(RankUserArenaVo rankArenaVo){
        if(rankArenaVo.arenaRecordList == null)return;
        int count = rankArenaVo.arenaRecordList.size();
        int length = count*(35+4) + 2;
        for(int i=0;i<count;i++){
            ArenaRecordPVo pVo = rankArenaVo.arenaRecordList.get(i);
            length += JkTools.getStringLength(pVo.name1);
            length += JkTools.getStringLength(pVo.name2);
        }
        byte[] bytes = new byte[length];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)count);
        for(ArenaRecordPVo pVo : rankArenaVo.arenaRecordList){
            pVo.toBytes(buffer);
//            buffer.putLong(pVo.tempID1);
//            JkTools.writeString(buffer,pVo.name1);
//            buffer.put(pVo.level1);
//            buffer.putInt(pVo.zdl1);
//
//            buffer.putLong(pVo.tempID2);
//            JkTools.writeString(buffer,pVo.name2);
//            buffer.put(pVo.level2);
//            buffer.putInt(pVo.zdl2);
//
//            buffer.putInt(pVo.time);
//            JkTools.writeBool(buffer,pVo.isWin);
        }
        AllSql.userSql.update(rankArenaVo.cacheUserVo.guid,AllSql.userSql.FIELD_ARENA,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    private static void airing(RankUserArenaVo arenaVo){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(8);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int index = arenaVo.orderIndex + 1;
        if(index%airingVo.divisor != 0)return;
        if(JkTools.compare(index,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&&JkTools.compare(index,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        StringBuffer sb = new StringBuffer();
        int[] data = Model.getDataInRange(1,index);
//        sb.append("恭喜 "+arenaVo.cacheUserVo.passportVo.name+" 成为竞技场第 "+index+" 名，获得: ");
        for(int i=0;i<data.length;i+=2) {
            PropBaseVo propBaseVo = Model.PropBaseMap.get(data[i]);
            if(propBaseVo == null)continue;
            switch (propBaseVo.quality){
                case 0 :sb.append("[D1D1D1]");
                    break;
                case 1 :sb.append("[57E157]");
                    break;
                case 2 :sb.append("[57BCE1]");
                    break;
                case 3 :sb.append("[D248FF]");
                    break;
                case 4 :sb.append("[EC9141]");
                    break;
                case 5 :sb.append("[FF4848]");
                    break;
            }
            sb.append(propBaseVo.name+"[-]*"+data[i+1]+"、");
        }
        if(sb.length()>0)
        sb.deleteCharAt(sb.length()-1);
        pVo.msg = airingVo.msg.replace("{1}",arenaVo.cacheUserVo.name).replace("{2}",String.valueOf(index)).replace("{3}",sb.toString());
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }
}


