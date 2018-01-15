package gluffy.comm;


 import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by jackie on 15-2-5.
 */
public abstract class AbsBaseBlobDeal {
    protected AbsUser absuser;
    public  void  loadData(String str) {

        if (str == null || str.length() < 2){
            loadDataBytes(null);
            return;
        }

        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        loadDataBytes(buffer);
    }
    protected   String  saveData(){
        byte[] bytes= saveDataBytes();
        if(bytes==null)
            return  null;
        return "'" + new BASE64Encoder().encode(bytes) + "'";
    }
    protected abstract  byte[]  saveDataBytes();

    protected abstract void  loadDataBytes(ByteBuffer buffer);

public  abstract   void saveSqlData();
    public  abstract   void unloadUser();

    public void initWithCache(AbsUser user) {
        this.absuser=user;
    }
}
