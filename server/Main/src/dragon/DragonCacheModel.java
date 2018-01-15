package dragon;

import gluffy.comm.BaseBlobDeal;
import protocol.*;
import sqlCmd.AllSql;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/18.
 */
public class DragonCacheModel extends BaseBlobDeal{
    public HashMap<Short, DragonPVo> dragonsMap;
    public DragonPVo mainDragon;
    public short getMainDragonID(){return mainDragon==null?0:mainDragon.baseID;}
    public DragonCacheModel(){
        dragonsMap = new HashMap<>();
    }
    @Override
    public void unloadUser() {
        user=null;
    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes=new byte[dragonsMap.size()*15+4];
        ByteBuffer buffer=ByteBuffer.wrap(bytes);
        buffer.putShort(getMainDragonID());
        buffer.putShort((short) dragonsMap.size());
        for (DragonPVo pvo : dragonsMap.values() ){
            pvo.toBytes(buffer);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer==null)return;
        int mainID=buffer.getShort();
        int count=buffer.getShort();
        for (int i = 0; i < count; i++) {
            DragonPVo pvo=new DragonPVo();
            pvo.fromBytes(buffer);
            if(pvo.baseID==mainID){
                mainDragon=pvo;
            }
            dragonsMap.put(pvo.baseID,pvo);
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user, AllSql.userSql.FIELD_DRAGON_CACHE, saveData());
    }
}

