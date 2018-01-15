package base;

import gluffy.comm.BaseBlobDeal;
import protocol.SkillPVo;
import protocol.TalentPVo;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillModel extends BaseBlobDeal{
     public HashMap<Integer, SkillPVo> skills;
    public HashMap<Byte, TalentPVo> talents;
     public SkillModel(){

        skills = new HashMap<>();
        talents=new HashMap<>();

      }

    @Override
    public void unloadUser() {
        user=null;
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes=new byte[skills.size()*6+1+talents.size()*6+1];
        ByteBuffer buffer=ByteBuffer.wrap(bytes);
        buffer.put((byte) skills.size());
        for (SkillPVo svo : skills.values()){
            svo.toBytes(buffer);
          }
        buffer.put((byte)talents.size());
        for (TalentPVo tvo : talents.values()){
            tvo.toBytes(buffer);

        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer==null)return;


        int bagCount=buffer.get();
        for (int i = 0; i < bagCount; i++) {
            SkillPVo svo=new SkillPVo();
            svo.fromBytes(buffer);
            skills.put(svo.baseID, svo);
        }
        int talentCount=buffer.get();
        for (int i = 0; i < talentCount; i++) {
            TalentPVo tvo=new TalentPVo();
            tvo.fromBytes(buffer);
             talents.put(tvo.baseID,tvo);
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user, AllSql.userSql.FIELD_SKILL,saveData());

    }


    public void loadData(ArrayList<SkillPVo> equipActiveSkills) {
        for (SkillPVo svo:equipActiveSkills){
             skills.put(svo.baseID, svo);
        }

    }
}
