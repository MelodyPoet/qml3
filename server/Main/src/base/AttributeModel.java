package base;

import gluffy.comm.BaseBlobDeal;
import sqlCmd.AllSql;
import table.AttributeEnum;

import java.nio.ByteBuffer;

/**
 * Created by admin on 2017/5/5.
 */
public class AttributeModel extends BaseBlobDeal{
    public int[] attribute;

    public AttributeModel(){
        attribute = new int[AttributeEnum.MAX];
    }

    @Override
    protected byte[] saveDataBytes() {
        short size = (short) attribute.length;
        byte[] bytes = new byte[size*4+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort(size);
        for(int att : attribute){
            buffer.putInt(att);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        short size = buffer.getShort();
        for(int i=0;i<size;i++){
            attribute[i] = buffer.getInt();
        }
    }

    @Override
    public void saveSqlData() {
        if(user == null)return;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_ATTRIBUTE,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
