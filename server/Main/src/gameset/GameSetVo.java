package gameset;

import arena.ArenaModel;
import gluffy.utils.JkTools;
import mine.MineModel;
import protocol.*;
import redness.RednessModel;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by admin on 2017/6/9.
 */
public class GameSetVo {
    public long guid;



    public void loadRedness(String str){
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
        RednessModel.campUserPVo = new CampUserPVo();
        RednessModel.campUserPVo.tempID = buffer.getLong();
        RednessModel.campUserPVo.baseID = buffer.get();
        AvatarPVo avatarPVo = new AvatarPVo();
        avatarPVo.weaponID = buffer.getInt();
        avatarPVo.skinID = buffer.getShort();
        RednessModel.campUserPVo.avata = avatarPVo;
        RednessModel.campUserPVo.name = JkTools.readString(buffer);
        byte size = buffer.get();
        for(int i=0;i <size;i++){
            RednessWinnerPVo rednessWinnerPVo = new RednessWinnerPVo();
            rednessWinnerPVo.name = JkTools.readString(buffer);
            rednessWinnerPVo.rednessMoney = buffer.getInt();
            rednessWinnerPVo.index = buffer.get();
            RednessModel.lastRednessRank.add(rednessWinnerPVo);
        }
        RednessModel.lastMailID = buffer.getLong();
    }

    public void saveRedness(){
        if(RednessModel.campUserPVo == null){
            AllSql.gameSetSql.update(this,AllSql.gameSetSql.FIELD_REDNESS,"''");
            return;
        }
        int size = JkTools.getStringLength(RednessModel.campUserPVo.name) + 2;
        for (RednessWinnerPVo rednessWinnerPVo : RednessModel.lastRednessRank) {
            size += JkTools.getStringLength(rednessWinnerPVo.name) + 2;
        }
        size += 16 + RednessModel.lastRednessRank.size() * 5 + 8;
        byte[] bytes = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(RednessModel.campUserPVo.tempID);
        buffer.put(RednessModel.campUserPVo.baseID);
        if (RednessModel.campUserPVo.avata == null) {
            buffer.putInt(0);
            buffer.put((byte) 0);
        } else {
            buffer.putInt(RednessModel.campUserPVo.avata.weaponID);
            buffer.putShort(RednessModel.campUserPVo.avata.skinID);
        }
        JkTools.writeString(buffer, RednessModel.campUserPVo.name);
        buffer.put((byte) RednessModel.lastRednessRank.size());
        for (RednessWinnerPVo rednessWinnerPVo : RednessModel.lastRednessRank) {
            JkTools.writeString(buffer, rednessWinnerPVo.name);
            buffer.putInt(rednessWinnerPVo.rednessMoney);
            buffer.put(rednessWinnerPVo.index);
        }
        buffer.putLong(RednessModel.lastMailID);
        AllSql.gameSetSql.update(this,AllSql.gameSetSql.FIELD_REDNESS,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadActive(String str){
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
        int count = buffer.get();
        for(int i=0;i<count;i++){
            ActiveVo vo = new ActiveVo();
            vo.startTime = buffer.getInt();
            vo.endTime = buffer.getInt();
            vo.isOpen = buffer.get() == 1?true:false;
            GameSetModel.timeMap.put(buffer.getInt(),vo);
        }
    }

    public void saveActive(){
        byte[] bytes = new byte[1+GameSetModel.timeMap.size()*13];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte) GameSetModel.timeMap.size());
        for(Map.Entry<Integer,ActiveVo> item: GameSetModel.timeMap.entrySet()){
            ActiveVo activeVo = item.getValue();
            buffer.putInt(activeVo.startTime);
            buffer.putInt(activeVo.endTime);
            buffer.put(activeVo.isOpen?(byte) 1:(byte) 0);
            buffer.putInt(item.getKey());
        }
        AllSql.gameSetSql.update(this,AllSql.gameSetSql.FIELD_ACTIVE,"'"+new BASE64Encoder().encode(bytes)+"'");
    }

    public void loadMine(String str){
        if (str == null || str.length() < 8){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int userCount = buffer.getShort();
        for(int i=0;i<userCount;i++){
            MineRankPVo mineRankPVo = new MineRankPVo();
            mineRankPVo.fromBytes(buffer);
            MineModel.lastMineList.add(mineRankPVo);
        }
        int gangCount = buffer.getShort();
        for(int i=0;i<gangCount;i++){
            MineRankPVo mineRankPVo = new MineRankPVo();
            mineRankPVo.fromBytes(buffer);
            MineModel.lastGangMineList.add(mineRankPVo);
        }
        int userMapCount = buffer.getInt();
        for(int i=0;i<userMapCount;i++){
            MineModel.lastUserMine.put(buffer.getLong(),buffer.getInt());
        }
    }

    public void saveMine(){
        int size = 8;
        for(MineRankPVo mineRankPVo : MineModel.lastMineList){
            size += JkTools.getStringLength(mineRankPVo.name)+2 + 17;
        }
        for(MineRankPVo mineRankPVo : MineModel.lastGangMineList){
            size += JkTools.getStringLength(mineRankPVo.name)+2 + 17;
        }
        byte[] bytes = new byte[size+MineModel.lastUserMine.size()*12];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) MineModel.lastMineList.size());
        for(MineRankPVo mineRankPVo : MineModel.lastMineList){
            mineRankPVo.toBytes(buffer);
        }
        buffer.putShort((short) MineModel.lastGangMineList.size());
        for(MineRankPVo mineRankPVo : MineModel.lastGangMineList){
            mineRankPVo.toBytes(buffer);
        }
        buffer.putInt((short) MineModel.lastUserMine.size());
        for(Map.Entry<Long,Integer> item : MineModel.lastUserMine.entrySet()){
            buffer.putLong(item.getKey());
            buffer.putInt(item.getValue());
        }
        AllSql.gameSetSql.update(this,AllSql.gameSetSql.FIELD_MINE,"'"+new BASE64Encoder().encode(bytes)+"'");
    }
}
