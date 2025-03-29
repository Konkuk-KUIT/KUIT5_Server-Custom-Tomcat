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

            // method, url 추출
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            int contentLength = 0;

            // Header 파싱
            String line;
            while (!(line = br.readLine()).equals("")) {
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            if (url.equals("/")) { // default
                url = "/index.html";
            }

            if (method.equals("GET") && handleSignUpGet(dos, url)) {
                return;
            }

            if (method.equals("POST") && url.equals("/user/signup")) {
                handleSignUpPost(br, dos, contentLength);
                return;
            }

            handleStaticResource(url, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleStaticResource(String url, DataOutputStream dos) throws IOException {
        String filePath = "./webapp" + url;
        File file = new File(filePath);

        if (file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } else {
            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    private void handleSignUpPost(BufferedReader br, DataOutputStream dos, int contentLength) throws IOException {
        char[] bodyData = new char[contentLength];
        br.read(bodyData, 0, contentLength);
        String body = new String(bodyData);

        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );
        MemoryUserRepository.getInstance().addUser(user);
        log.info("New user signed up (POST): " + user.getUserId());

        response302Header(dos, "/index.html");
    }

    private boolean handleSignUpGet(DataOutputStream dos, String url) {
        if (url.startsWith("/user/signup")) {
            // 쿼리 파라미터 파싱
            String queryString = url.substring(url.indexOf("?") + 1);
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

            // User 객체 생성 후 저장
            User user = new User(
                    params.get("userId"),
                    params.get("password"),
                    params.get("name"),
                    params.get("email")
            );
            MemoryUserRepository.getInstance().addUser(user);
            log.info("New user signed up: " + user.getUserId());

            // index.html로 redirect
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
