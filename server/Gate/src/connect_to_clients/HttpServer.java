package connect_to_clients;

import comm.Client;
import comm.QuickProtocal;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class HttpServer extends HttpServlet {


    public QuickProtocal currentProtocal = new QuickProtocal();
    private Consumer<QuickProtocal> onRecvClient;

    public static void init(int port, Consumer<QuickProtocal> onRecvClient) {

        Server server = new Server(port);


        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        server.setHandler(context);

        HttpServer httpServer = new HttpServer();
        httpServer.onRecvClient = onRecvClient;
        context.addServlet(new ServletHolder(httpServer), "/game");

        try {
            server.start();

            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendToClient(QuickProtocal quickProtocal) throws IOException {
        long guid = quickProtocal.guid;

        Client client = Client.getOne(guid);
        if (client == null) {
            return;
        }


        OutputStream outputStream = client.currentHttp.getOutputStream();
        System.out.println("sendToClient:" + (quickProtocal.getDataLength() - quickProtocal.offset));
        outputStream.write(quickProtocal.byteBuffer.array(), quickProtocal.offset, quickProtocal.getDataLength() - quickProtocal.offset);

        //outputStream.flush();
        client.currentHttpCt.complete();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        byte[] bytes = new byte[256 * 8];//readBytes.poll();
        //readBytes.offer(bytes);
      int len=  request.getInputStream().read(bytes);
        currentProtocal.fromBytes(ByteBuffer.wrap( bytes),len);
        long  guid = currentProtocal.guid;
         // short proID=	buffer.getShort();
        //  byte clientTempID =buffer.get();
        // System.out.println("uid="+uid);
        Client client = Client.getOne(guid);
        if (client == null) {
            client = new Client();
            client.guid = guid;
             Client.setOne(guid, client);
        }
        client.currentHttp = response;
        client.lastRqstTime = System.currentTimeMillis();
        onRecvClient.accept(currentProtocal);
        final Continuation continuation = ContinuationSupport.getContinuation(request);

        // 判断是否超时
        if (continuation.isExpired()) {
            // 返回超时Response
            //  sendMyTimeoutResponse(response);
            return;
        }

        // 挂起HTTP连接
        continuation.suspend(response); // response被包装
        client.currentHttpCt = continuation;
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //response.getOutputStream()

    }
}
