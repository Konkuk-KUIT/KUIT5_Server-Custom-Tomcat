package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_PATH = "./webapp";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : "
                + connection.getInetAddress()
                + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // 추가부분
            // 요청 헤더 읽기
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            // 요청에서 파일 경로 추출
            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) {
                return;
            }

            String requestPath = tokens[1];
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }
            // 파일 객체 생성
            File file = new File(WEBAPP_PATH + requestPath);
            // 파일 존재 확인 후 응답 보냄
            if (file.exists() && !file.isDirectory()) {
                byte[] body = Files.readAllBytes(file.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else {
                // 파일 없을 시 404 응답 보냄
                response404(dos);
            }

//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            // 브라우저가 HTML로 인식하게 함
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            // 파일 크기 명시 -> 브라우저가 어디까지 읽어야 할 지 알 수 있도록
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 본문(HTML) 전송
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            // 바이트 데이터를 처음부터 끝까지 읽어서 클라이언트에게 보냄
            dos.write(body, 0, body.length);
            dos.flush(); // 데이터 강제 출력
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404(DataOutputStream dos) throws IOException {
        String notFoundMessage = "<html><body></h1>404 Not Found</h1></body></html>";
        byte[] body = notFoundMessage.getBytes();
        dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }


}
