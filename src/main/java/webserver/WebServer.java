package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;
    private static final Logger log = Logger.getLogger(WebServer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){

            // 연결 소켓
            Socket connection;
            // ServerSock welcomSocket이 연결되면 welcomeSocket.accpet()로 연결소켓 반환
            while ((connection = welcomeSocket.accept()) != null) {
                // 스레드에 작업 전달
                // RequestHandler는 Runnable을 구현했기 때문에 submit()에 넘길 수 있다.
                // ExecutorService는 내부적으로 스레드 풀을 가지고 있어서, 요청마다 새로운 스레드에서 run()을 호출하는 것이 가능 --> 장점이 무엇?
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
