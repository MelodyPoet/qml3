import comm.*;
import gluffy.utils.CpuDebuger;
import gluffy.utils.LogManager;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


import java.io.File;
import java.util.Calendar;

/**
 * Created by jackie on 14-4-16.
 */

public class GameServer  {
    //private static ArrayBlockingQueue<byte[]> readBytes=new ArrayBlockingQueue<>(50);
    private static byte[] reloginBytes;


    public static void main(String[] args) throws Exception {
        LogManager.init();
        CheckUtils.init();
        SAXBuilder builder = new SAXBuilder();
        CpuDebuger.init(0, 0);

        Document doc = builder.build(new File("config/config.xml"));

        Element rootEl = doc.getRootElement();
        long serverID = Long.parseLong(rootEl.getChild("serverID").getValue());
        Model.configXmlDoc = doc;
        Model.openDay = Calendar.getInstance();

        new SysLoop().start();
        CpuDebuger.print("SysLoop", 0);

      //  new UdpGameServer(port + 1).start();
        CpuDebuger.print("gameServerStart", 0);

        //  server.join();

        new MessageServer();

    }




}
