package skin;

import gluffy.comm.BaseBlobDeal;
import protocol.SkinPVo;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by admin on 2016/8/26.
 */
public class SkinModel extends BaseBlobDeal{

    public HashMap<Short, SkinPVo> skinMap = new HashMap<>();

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[skinMap.size() * 5 + 2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short)skinMap.size());
        for(SkinPVo skinPVo : skinMap.values()){
            skinPVo.toBytes(buffer);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        int count = buffer.getShort();
        for(int i=0;i<count;i++){
            SkinPVo skinPVo = new SkinPVo();
            skinPVo.fromBytes(buffer);
            skinMap.put(skinPVo.skinID,skinPVo);
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_SKIN_LIST,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
