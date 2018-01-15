package comm;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class QuickProtocal {
     public ByteBuffer byteBuffer;

    public byte rqstType;
    public byte rqstTempID;


   // public String uid;
   // public short serverID;
    public  long guid;
    public short proID;

    private int dataLength;
    public int offset;

     public boolean  castToClinet;
     public ArrayList<Long> castGroup;
 // gate to client



public int getDataLength(){return dataLength;}

    public void fromBytes(ByteBuffer byteBuffer,int dataLength) {
        this.byteBuffer = byteBuffer;
this.dataLength=dataLength;

        castGroup=null;
        guid = byteBuffer.getLong();
        castToClinet=false;
        //-1 广播
        if(guid==-1){

            castGroup=Utils.readArray(byteBuffer,Long.class);
            castToClinet=true;
        }
        offset = byteBuffer.position();

        rqstType = byteBuffer.get();//1=重要数据需要返回的协议，
        // 2=不重要不需要返回协议，3=收到确认协议 ，4=转发服务
            rqstTempID = 0;
            if (rqstType < 1 || rqstType > 4) {
                return;

            }
            if (rqstType == 1||rqstType==3) {

                rqstTempID = byteBuffer.get();
            }


            if (byteBuffer.remaining() >= 2) {
                proID = byteBuffer.getShort();
            }


    }
}
