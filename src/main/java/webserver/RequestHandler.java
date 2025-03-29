package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // 요청 라인 읽기
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            log.info("Request Line: " + requestLine);

            handleRequest(requestLine, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleRequest(String requestLine, DataOutputStream dos) throws IOException { // URI에 해당하는 Html 연결
        // 요청 URI 파싱
        String[] tokens = requestLine.split(" ");
        String url = tokens[1];

        // "/" 요청 → index.html로 변경 defalut로 정해주는 html
        if (url.equals("/")) {
            url = "/index.html";
        }

        if (handleSignUpRequest(dos, url)) return;

        // 파일 경로 지정
        String filePath = "./webapp" + url;

        File file = new File(filePath);
        if (file.exists()) { // 파일이 존재하면 내용 읽어서 반환
            byte[] body = Files.readAllBytes(file.toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } else {
            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    private boolean handleSignUpRequest(DataOutputStream dos, String url) {
        if (url.startsWith("/user/signup")) {
            // 1. 쿼리 파라미터 파싱
            String queryString = url.substring(url.indexOf("?") + 1);
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

            // 2. User 객체 생성 후 저장
            User user = new User(
                    params.get("userId"),
                    params.get("password"),
                    params.get("name"),
                    params.get("email")
            );
            MemoryUserRepository.getInstance().addUser(user);
            log.info("New user signed up: " + user.getUserId());

            // 3. index.html로 redirect
            response302Header(dos, "/index.html");
            return true;
        }
        return false;
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
