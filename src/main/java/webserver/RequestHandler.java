package webserver;

import controller.*;
import http.model.HttpRequest;
import http.model.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Socket connection;
    private final RequestMapper requestMapper;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper();  // RequestMapper 인스턴스 생성
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequest.from(br); // HttpRequest 생성
            HttpResponse httpResponse = new HttpResponse(out); // HttpResponse 생성

            // URL에 해당하는 컨트롤러를 RequestMapper에서 가져옴
            Controller controller = requestMapper.getController(httpRequest.getPath());

            // 해당하는 컨트롤러가 있다면 실행
            if (controller != null) {
                controller.execute(httpRequest, httpResponse);
            } else {
                try {
                    // 컨트롤러 없으면 정적 리소스로 처리 시도
                    httpResponse.forward(httpRequest.getPath());
                } catch (IOException e) {
                    // 정적 파일도 없으면 진짜 404 처리
                    httpResponse.forward("/404.html");
                }
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
