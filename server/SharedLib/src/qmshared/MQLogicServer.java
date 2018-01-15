package qmshared;

import org.zeromq.ZMQ;

import java.util.function.Consumer;

public class MQLogicServer extends Thread {
    public static QuickProtocal currentProtocal = new QuickProtocal();
    static ZMQ.Socket toGate;
    static ZMQ.Socket pull;
    private Consumer<QuickProtocal> onRecvGate;

    public MQLogicServer(int port, Consumer<QuickProtocal> onRecvGate) {
        this.onRecvGate = onRecvGate;
        toGate = ZMQ.context(1).socket(ZMQ.PUSH);
        toGate.connect("tcp://" + Model.GateIp + ":" + Model.GatePort);
        pull = ZMQ.context(1).socket(ZMQ.PULL);
        pull.connect("tcp://" + Model.GateIp + ":" + port);
    }

    public static void sendToGate(byte[] bytes) {

        toGate.send(bytes, ZMQ.NOBLOCK);
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            byte[] bytes = pull.recv();
            if (bytes != null) {
                currentProtocal.fromBytes(bytes);
                currentProtocal.proID = (short) Math.abs(currentProtocal.proID);
                onRecvGate.accept(currentProtocal);

            }

        }

    }


}



