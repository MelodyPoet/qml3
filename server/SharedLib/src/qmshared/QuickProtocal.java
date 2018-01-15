package   qmshared ;

import java.nio.ByteBuffer;

public class QuickProtocal {

        public ByteBuffer byteBuffer;
    public  long guid;
      public short proID;


    public QuickProtocal() {

    }

     public void fromBytes(byte[] bytes) {
        this.byteBuffer=ByteBuffer.wrap(bytes);


          guid = byteBuffer.getLong();
       byte   rqstType = byteBuffer.get();//1=重要数据需要返回的协议，2=不重要不需要返回协议，3=收到确认协议,4=转发服务


         if (rqstType == 1||rqstType == 3) {

              byteBuffer.get();
         }

             proID = byteBuffer.getShort();



    }

}
