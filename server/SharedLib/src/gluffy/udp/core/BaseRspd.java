package gluffy.udp.core;


import gluffy.comm.AbsClient;
import gluffy.utils.JkTools;
import qmshared.MQLogicServer;
import qmshared.Utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BaseRspd {

   public static ByteBuffer bytes=ByteBuffer.wrap(new byte[1024*20]);

    public  short protocolID;

  public   boolean isImportant=false;
     private AbsClient client;
     public static  Collection<Long> castGroup;
 public  static  void  tempCast( Collection<Long> castGroup){
    BaseRspd.castGroup=castGroup;
    }
    public BaseRspd(){}
     public BaseRspd(AbsClient client, short protocolID, boolean isImportant) {
         bytes.clear();
         this.isImportant=isImportant;
         this.client=client;
         this.protocolID = protocolID;



         //这2项目 只有转发需要 发客户端时候剔除 所以放最开头
         if (castGroup == null) {
             bytes.putLong(client.guid);
         } else {
             bytes.putLong(-1);//-1 表示广播
             JkTools.writeArray(bytes,castGroup);
             castGroup=null;
         }

        //服务器直接转发时候用作 逻辑服务器id
        if(isImportant) {
            bytes.put((byte)(1));
            bytes.put((byte)0);//init for tempid
        }else {
            bytes.put((byte)(2));
        }
        bytes.putShort(protocolID);

    }

    protected void finish() {
        if(castGroup!=null&&castGroup.size()==0){
            castGroup=null;
            return;
        }
        int len=bytes.position();
        bytes.limit(len);
        MQLogicServer.sendToGate(Arrays.copyOf(bytes.array(), bytes.position()));
        System.out.println("sendToGate:"+protocolID);
}
}



