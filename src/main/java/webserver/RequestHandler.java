package webserver;

import db.MemoryUserRepository;
import http.util.*;
import model.User;
import model.UserParam;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequest.from(br);
            DataOutputStream dos = new DataOutputStream(out);

            // MAPPER -> CONTROLLER -> LOGIN, SIGNUP
            String url = httpRequest.getPath();
            String method = httpRequest.getMethod();

            if (url.equals("/")) {
                url = Route.INDEX.path();
            }

            ///요구사항 2: get으로 회원가입 쿼리스트링처리
            if (HttpMethod.isEqual(method, HttpMethod.GET) && url.equals(Route.SIGN_UP.path())) {
                String queryString = httpRequest.getQueryString();

                // 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

                User user = new User(
                        params.get(UserParam.USER_ID.key()),
                        params.get(UserParam.PASSWORD.key()),
                        params.get(UserParam.NAME.key()),
                        params.get(UserParam.EMAIL.key())
                );

                MemoryUserRepository.getInstance().addUser(user);
                System.out.println("회원가입 성공: " + user.getUserId());

                response302Header(dos, Route.INDEX.path());
                return;
            }

            /// 요구사항 3: POST 방식으로 회원가입
            if (HttpMethod.isEqual(method, HttpMethod.POST) && url.equals(Route.SIGN_UP.path())) {

                // 본문 읽기
                String body = httpRequest.getBody(); // ⭐
                System.out.println("Post_Body 내용: " + body);

                // 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                User user = new User(
                        params.get(UserParam.USER_ID.key()),
                        params.get(UserParam.PASSWORD.key()),
                        params.get(UserParam.NAME.key()),
                        params.get(UserParam.EMAIL.key())
                );

                MemoryUserRepository.getInstance().addUser(user);
                System.out.println("POST 회원가입 성공: " + user.getUserId());

                /// 요구사항 4: 302 status code 적용
                response302Header(dos, Route.INDEX.path());
                return;
            }


            /// 요구사항 5: 로그인하기
            if (HttpMethod.isEqual(method, HttpMethod.POST) && url.equals("/user/login")) {// Route.LOGIN 못쓰는거 해결하기
                String body = httpRequest.getBody();
                System.out.println("Post_Body 내용: " + body);

                // 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                String userId = params.get(UserParam.USER_ID.key());
                String password = params.get(UserParam.PASSWORD.key());

                // 회원 조회
                User user = MemoryUserRepository.getInstance().findUserById(userId);

                if (user != null && user.getPassword().equals(password)) {
                    // 로그인 성공 → index.html로 리다이렉트, 쿠키 설정
                    response302HeaderWithCookie(dos, Route.INDEX.path(), "logined=true");
                    System.out.println("로그인 성공: " + userId);
                } else {
                    // 로그인 실패 → login_failed.html로 리다이렉트
                    response302Header(dos, Route.LOGIN_FAILED.path());
                    System.out.println("로그인 실패: " + userId);
                }
                return;
            }

            /// 요구사항 6: 사용자 목록 출력
            if (url.equals(Route.USER_LIST.path())) {
                boolean isLogined = false;
                String cookies = httpRequest.getHeaders().get(HttpHeader.COOKIE.key());
                if (cookies != null) {
                    String[] cookieArray = cookies.split(";");
                    for (String cookie : cookieArray) {
                        String[] pair = cookie.trim().split("=");
                        if (pair.length == 2 && pair[0].equals("logined") && pair[1].equals("true")) {
                            isLogined = true;
                            break;
                        }
                    }
                }


                if (isLogined) {
                    url = Route.LIST.path();
                } else {
                    response302Header(dos, Route.LOGIN.path());
                    return;
                }
            }


            String filePath = "./webapp" + url;
            File file = new File(filePath);

            if (file.exists()) {
                byte[] body = Files.readAllBytes(file.toPath());
                String contentType = getContentType(filePath); // Content-Type 추출
                response200Header(dos, body.length, contentType); // 200 헤더 전송
                responseBody(dos, body);
            } else {
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /// 요구사항 4: 302 status code 적용
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            System.out.println(dos);


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND + "\r\n");//공백!!!!!!!!!!!!11!!!!
            dos.writeBytes(HttpHeader.CONTENT_TYPE + ": " + "text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE + ": " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
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

    private String getContentType(String filePath) {
        return ContentType.fromFilePath(filePath);
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION + ": " + path + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE + ": " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}