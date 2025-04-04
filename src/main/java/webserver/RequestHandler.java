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
            HttpResponse httpResponse = new HttpResponse(out);

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

                httpResponse.redirect(Route.INDEX.path());
                return;
            }

            /// 요구사항 3: POST 방식으로 회원가입
            if (HttpMethod.isEqual(method, HttpMethod.POST) && url.equals(Route.SIGN_UP.path())) {

                // 본문 읽기
                String body = httpRequest.getBody();
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
                httpResponse.redirect(Route.INDEX.path());
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
                    httpResponse.redirectWithCookie(Route.INDEX.path(), "logined=true");
                    System.out.println("로그인 성공: " + userId);
                } else {
                    // 로그인 실패 → login_failed.html로 리다이렉트
                    httpResponse.redirect(Route.LOGIN_FAILED.path());
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
                    httpResponse.redirect(Route.LOGIN.path());
                    return;
                }
            }

            httpResponse.forward(url);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}