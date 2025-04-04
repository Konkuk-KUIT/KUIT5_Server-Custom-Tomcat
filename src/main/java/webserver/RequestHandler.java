package webserver;

import db.MemoryUserRepository;
import http.*;
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

            HttpRequest request = HttpRequest.from(br);

            HttpMethod method = HttpMethod.from(request.getMethod());
            String url = request.getPath();
            Map<String, String> headers = request.getHeaderLine(); // or Map<String, String> headers = request.getHeaders();

            if (url.equals(UrlPath.ROOT.getPath())) {
                url = UrlPath.INDEX.getPath();
            }

            UrlPath matchedPath = UrlPath.from(url);

            switch (matchedPath) {
                case SIGNUP:
                    if (method == HttpMethod.POST) {
                        log.info("회원가입 선택");
                        handleSignUp(request.getBody(), dos); // 바디가 이미 정리된 상태이므로, String 값을 바로 넘겨줌.
                        return;
                    }
                    break;

                case LOGIN:
                    if (method == HttpMethod.POST) {
                        log.info("로그인 선택");
                        handleLogin(request.getBody(), dos);
                        return;
                    }
                    break;

                case USER_LIST:
                    if (!isLoggedIn(request.getHeaderLine())) { // 쿠키 검사
                        log.info("로그인하지 않은 사용자의 userList 접근");
                        response302Header(dos, UrlPath.LOGIN_HTML.getPath());
                    } else {
                        log.info("로그인한 사용자의 userList 접근");
                        response302Header(dos, UrlPath.USER_LIST_HTML.getPath());
                    }
                    return;

                default:
                    if (url.endsWith(".css")) {
                        handleCssRequest(url, dos);
                        return;
                    }

                    handleStaticResource(url, dos);
            }

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

    private void handleLogin(String body, DataOutputStream dos) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = MemoryUserRepository.getInstance().findUserById(params.get(UserQueryKey.USER_ID.getKey()));

        // Todo 검증 로직 추후에 다 따로 빼기
        if (user == null) { // 존재하지 않는 회원, 로그인 실패
            log.info("user login failed");
            response302Header(dos, UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        if(!user.getPassword().equals(params.get(UserQueryKey.PASSWORD.getKey()))) { // 비밀번호 불일치, 로그인 실패
            log.info("user login failed");
            response302Header(dos, UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        response302Header(dos, UrlPath.INDEX.getPath(), "logined=true");

        log.info("user login success!: " + user.getUserId());
    }

    private void handleSignUp(String body, DataOutputStream dos) {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

        User user = new User(
                params.get(UserQueryKey.USER_ID.getKey()),
                params.get(UserQueryKey.PASSWORD.getKey()),
                params.get(UserQueryKey.NAME.getKey()),
                params.get(UserQueryKey.EMAIL.getKey())
        );

        MemoryUserRepository.getInstance().addUser(user);
        log.info("New user signed up (POST): " + user.getUserId());

        response302Header(dos, "/index.html");
    }

    private boolean isLoggedIn(Map<String, String> headers) {
        String cookieLine = headers.get("Cookie");
        if (cookieLine == null) {
            return false;
        }

        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieLine);
        return "true".equals(cookies.get("logined"));
    }



    // Todo response header 클래스 따로 빼기
    private void response302Header(DataOutputStream dos, String path, String cookie) { // 쿠키 오버로딩
        try {
            dos.writeBytes(HttpStatus.FOUND.getStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.format(path));
            dos.writeBytes(HttpHeader.SET_COOKIE.format(cookie));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpStatus.FOUND.getStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.format(path));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpStatus.NOT_FOUND.getStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/html;charset=utf-8"));
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(lengthOfBodyContent)));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(HttpStatus.OK.getStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/html;charset=utf-8"));
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(lengthOfBodyContent)));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) { // css인자 추가
        try {
            dos.writeBytes(HttpStatus.OK.getStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/css;charset=utf-8"));
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(lengthOfBodyContent)));
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
