package elit;

import comm.User;
import gluffy.comm.BaseBlobDeal;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * Created by admin on 2017/4/20.
 */
public class EliteModel extends BaseBlobDeal{

    public HashSet<Short> elitePassedMap;
    public User user;


    public EliteModel(User user){
        this.user = user;
        elitePassedMap = new HashSet<>();
    }
    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[elitePassedMap.size()*2+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) elitePassedMap.size());
        for(short mapID : elitePassedMap){
            buffer.putShort(mapID);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int count = buffer.getShort();
        for(int i=0;i<count;i++){
            elitePassedMap.add(buffer.getShort());
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_ELITE,saveData());
    }

    @Override
    public void unloadUser() {
        this.user = null;
    }
}
