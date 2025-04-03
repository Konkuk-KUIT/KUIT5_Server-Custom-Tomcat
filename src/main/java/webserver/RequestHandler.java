package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPP_PATH = "./webapp";
    private final MemoryUserRepository userRepository = MemoryUserRepository.getInstance();

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

            String method = tokens[0];
            String requestPath = tokens[1];
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

//            // 요구사항 2 - 유저 생성인지 index.html 파일 띄울지 결정
//            if (requestPath.startsWith("/user/create?")) {
//                handleUserCreation(requestPath, dos);
//            } else {
//                serveStaticFile(requestPath, dos);
//            }

            // 요구사항 3 - POST 방식으로 회원가입하기
            if (method.equals("POST") && requestPath.equals("/user/create")) {
                handleUserCreation(br, dos);
            } else if (method.equals("POST") && requestPath.equals("/user/login")) {
                // 로그인 처리 (요구사항 5)
                handleUserLogin(br, dos);
            } else if (requestPath.endsWith(".html")){
                serveStaticFile(requestPath, dos);
            } else {
                response404(dos);
            }

//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleUserLogin(BufferedReader br, DataOutputStream dos) throws IOException {
        // 요청 본문 읽기
        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }

        // 요청 본문 파싱
        char[] bodyChars = new char[requestContentLength];
        br.read(bodyChars);
        String requestBody = new String(bodyChars);

        String userId = null, password = null;
        for (String param : requestBody.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equals("userId")) {
                    userId = keyValue[1];
                }
                if (keyValue[0].equals("password")) {
                    password = keyValue[1];
                }
            }
        }

        // 유저 검증
        User user = userRepository.findUserById(userId);
        if (user != null && user.getPassword().equals(password)) {
            // 로그인 성공
            response302RedirectWithCookie(dos, "/index.html", "logined=true");
        } else {
            // 로그인 실패
            response302Redirect(dos, "/logined_failed.html");
        }
    }

    // 회원가입 처리 메서드

    private void handleUserCreation(BufferedReader br, DataOutputStream dos) throws IOException {
        // 요구사항 3의 request body 읽기 위한 코드
        // HTTP 요청 헤더 읽기
        int requestContentLength = 0;

        while (true) {
            final String line = br.readLine();
            // line.equals("")에서 수정
            if (line == null || line.isEmpty()) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]); // 길이만 int로 추출
            }
        }

        // HTTP 요청 본문 읽기
        // 요청길이만큼 배열 생성
        char[] bodyChars = new char[requestContentLength];
        br.read(bodyChars); // 읽은 문자 개수를 반환
        String requestBody = new String(bodyChars); // char 배열을 문자열로 변환

        String userId = "", password = "", name = "", email = "";

        for (String param : requestBody.split("&")) {// 키-값 쌍 배열 순회
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                switch (keyValue[0]) {
                    case "userId": userId = keyValue[1]; break;
                    case "password": password = keyValue[1]; break;
                    case "name": name = keyValue[1]; break;
                    case "email": email = keyValue[1]; break;
                }
            }
            userRepository.addUser(new User(userId, password, name, email));
            response302Redirect(dos, "/index.html");
        }
    }
    private void serveStaticFile(String requestPath, DataOutputStream dos) throws IOException {
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
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException{
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
    private void response302Redirect(DataOutputStream dos, String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found\r\n");
        // body(Content-Type, Content-Length) 없이 Location 헤더만 필요
        dos.writeBytes("Location: " + location + "\r\n");
        dos.writeBytes("\r\n");
    }

    // 302 Redirect + Cookie 응답
    private void response302RedirectWithCookie(DataOutputStream dos, String location, String cookie) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found\r\n");
        dos.writeBytes("Location: " + location + "\r\n");
        dos.writeBytes("Set-Cookie" + cookie + "\r\n");
        dos.writeBytes("\r\n");
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
