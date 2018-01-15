package wing;

import gluffy.comm.BaseBlobDeal;
import protocol.WingCorePVo;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by admin on 2017/4/27.
 */
public class WingModel extends BaseBlobDeal{
    public HashSet<Short> wingSet = new HashSet<>();
    public HashMap<Byte,WingCorePVo> coreMap = new HashMap<>();
    public Short equipWingID=0;

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[5+coreMap.size()*7+wingSet.size()*2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) wingSet.size());
        for(short id : wingSet){
            buffer.putShort(id);
        }
        buffer.put((byte) coreMap.size());
        for(WingCorePVo wingCorePVo : coreMap.values()){
            wingCorePVo.toBytes(buffer);
        }
        buffer.putShort(equipWingID);
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int count = buffer.getShort();
        for(int i=0;i<count;i++){
            wingSet.add(buffer.getShort());
        }
        int coreCount = buffer.get();
        for(int i=0;i<coreCount;i++){
            WingCorePVo wingCorePVo = new WingCorePVo();
            wingCorePVo.fromBytes(buffer);
            coreMap.put(wingCorePVo.id,wingCorePVo);
        }
        equipWingID = buffer.getShort();
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_WING,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
