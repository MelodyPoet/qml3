package realm;

import comm.User;
import gluffy.comm.BaseBlobDeal;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class RealmModel extends BaseBlobDeal{
    public User user;
    public HashMap<Byte,RealmVo> realmMap = new HashMap<>();
    public static int MAX_CYCLONE = 10;
    public RealmModel(User user) {
        this.user = user;
    }

    @Override
    protected byte[] saveDataBytes() {
        int size = 0;
        for(RealmVo realmVo : realmMap.values()){
            size += 4+2+realmVo.cycloneUp.size()+realmVo.danMap.size()*3;
        }
        size += 1;
        byte[] bytes = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for(RealmVo realmVo : realmMap.values()){
            realmVo.toBytes(buffer);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int count = buffer.get();
        for(int i=0;i<count;i++){
            RealmVo realmVo = new RealmVo();
            realmVo.fromBytes(buffer);
            realmMap.put(realmVo.realm,realmVo);
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_REALM,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
