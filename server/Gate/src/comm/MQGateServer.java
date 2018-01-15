package comm;


import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

public class MQGateServer extends Thread {
 public  static    MQGateServer instance;
    public QuickProtocal currentProtocal = new QuickProtocal();
  public   ZMQ.Socket toMain = ZMQ.context(1).socket(ZMQ.PUSH);
  public   ZMQ.Socket toScene = ZMQ.context(1).socket(ZMQ.PUSH);

    ZMQ.Socket pull;
    private Consumer<QuickProtocal> onRecvLogic;

    public MQGateServer(Consumer<QuickProtocal> onRecvLogic) {
        instance=this;
        this.onRecvLogic = onRecvLogic;
        ZMQ.Context context = ZMQ.context(1);
        pull = context.socket(ZMQ.PULL);
        //  pull.bind("ipc://gate");
        pull.bind("tcp://0.0.0.0:" + Model.GatePort);
        toMain.bind("tcp://0.0.0.0:" + Model.MainPort);
        toScene.bind("tcp://0.0.0.0:" + Model.ScenePort);

    }

    public void sendToLogic(QuickProtocal protocal) {
        //这里 复复制也浪费 所以后期用新版api支持 范围发送
        byte[] bytes= Arrays.copyOf(protocal.byteBuffer.array(),protocal.getDataLength());
        if( protocal.proID<12700){
            sendToLogic(bytes,toMain);

        }else if( protocal.proID<12800){
            sendToLogic(bytes,toScene);
        }
    }
    public void sendToLogic( byte[] bytes,ZMQ.Socket target) {


        int flag = ZMQ.NOBLOCK;

        target.send(bytes,flag);

    }

    @Override
    public void run() {
        super.run();

        while (true) {
            byte[] bytes = pull.recv();
            if (bytes != null && bytes.length > 0) {
                //需要区分http返回 还是 udp返回
                currentProtocal.fromBytes(ByteBuffer.wrap(bytes),bytes.length);

                onRecvLogic.accept(currentProtocal);

            }

        }

    }


}



