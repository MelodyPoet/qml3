package giftCode;

import comm.User;
import gluffy.comm.BaseBlobDeal;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * Created by admin on 2017/5/11.
 */
public class GiftCodeModel extends BaseBlobDeal{
    public HashSet<Integer> usedgift = new HashSet<>();
    public User user;
    public GiftCodeModel(User user){
        this.user = user;
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[usedgift.size()*4+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)usedgift.size());
        for(int giftID : usedgift){
            buffer.putInt(giftID);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int count = buffer.getShort();
        for(int i=0;i<count;i++){
            usedgift.add(buffer.getInt());
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_USED_GIFT,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
