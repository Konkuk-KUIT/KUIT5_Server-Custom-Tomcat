package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
            List<String> headers = new ArrayList<>();
            // BufferedReader는 한 번 읽고 나면 커서가 앞으로 움직여서 다시 읽기 어려움
            // 그러므로 헤더 먼저 모두 읽고 저장해두기
            String line;
            while (!(line = br.readLine()).equals("")) {
                headers.add(line);
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            if (url.equals("/")) { // default
                url = "/index.html";
            }

            if (method.equals("POST") && url.equals("/user/signup")) { // POST 회원가입
                log.info("회원가입 선택");
                handleSignUp(br, dos, contentLength);
                return;
            }

            if (method.equals("POST") && url.equals("/user/login")) { // 로그인
                log.info("로그인 선택");
                handleLogin(br, dos, contentLength);
                return;
            }

            if (url.equals("/user/userList")) {
                if(!isLoggedIn(headers)){
                    log.info("로그인하지 않은 사용자의 userList 접근");
                    response302Header(dos, "/user/login.html");
                } else {
                    response302Header(dos, "/user/list.html");
                    log.info("로그인한 사용자의 userList 접근");
                }
                return;
            }

            if(url.endsWith("css")){
                handleCssRequest(url, dos);
            }

            handleStaticResource(url, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleCssRequest(String url, DataOutputStream dos) throws IOException{
        log.info("css 파일 요청 들어옴");
        String filePath = "./webapp" + url;
        File file = new File(filePath);

        if (file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            response200Header(dos, body.length, "CSS");
            responseBody(dos, body);
            log.info("css 전송 성공");
        } else {
            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);
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

    private void handleLogin(BufferedReader br, DataOutputStream dos, int contentLength) throws IOException {
        char[] bodyData = new char[contentLength];
        br.read(bodyData, 0, contentLength);
        String body = new String(bodyData);

        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

        // Todo 검증 로직 추후에 다 따로 빼기
        if (user == null) { // 존재하지 않는 회원, 로그인 실패
            log.info("user login failed");
            response302Header(dos, "/user/login_failed.html");
            return;
        }

        if(!user.getPassword().equals(params.get("password"))) { // 비밀번호 불일치, 로그인 실패
            log.info("user login failed");
            response302Header(dos, "/user/login_failed.html");
            return;
        }

        response302Header(dos, "/index.html", "logined=true");

        log.info("user login success!: " + user.getUserId());
    }

    private void handleSignUp(BufferedReader br, DataOutputStream dos, int contentLength) throws IOException {
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

    private boolean isLoggedIn(List<String> headers) {
        for (String line : headers) {
            if (line.startsWith("Cookie:")) {
                String cookieLine = line.substring("Cookie:".length()).trim();
                Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieLine);
                return "true".equals(cookies.get("logined"));
            }
        }
        return false; // Cookie가 없거나 logined=true가 아닌 경우
    }


    // Todo response header 클래스 따로 빼기
    private void response302Header(DataOutputStream dos, String path, String cookie) { // 쿠키 추가해서 오버로드한 메소드
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String css) { // css 인자 추가해서 오버로드한 메소드
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
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
