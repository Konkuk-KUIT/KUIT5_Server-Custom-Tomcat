package webserver;

import db.MemoryUserRepository;
import http.util.HttpMethod;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import http.util.HttpRequest;


public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {

            //log.info(" Stream opened");

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpRequest request = HttpRequest.from(br);
            //log.info("Request parsed - Path: " + request.getPath());

            HttpResponse response = new HttpResponse(new DataOutputStream(out));
            //log.info("Response stream prepared");

            log.info("[요청] Method: " + request.getMethod());
            log.info("[요청] Path: " + request.getPath());
            log.info("[요청] Headers: " + request.getHeaders());
            log.info("[요청] Query Params: " + request.getQueryParams());
            log.info("[요청] Body Params: " + request.getBodyParams());

            String path = request.getPath();

            HttpMethod method = request.getMethod();
            path = request.getPath();

            if (path.equals("/")) {
                response.forward("/index.html");
                return;
            }

            // 회원가입 폼 요청
            if (method == HttpMethod.GET && path.equals("/user/form.html")) {
                response.forward("/user/form.html");
                return;
            }

            // GET 방식 회원가입 처리
            if (method == HttpMethod.GET && path.equals("/user/signup")) {
                Map<String, String> params = request.getQueryParams();
                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );
                MemoryUserRepository.getInstance().addUser(user);
                response.redirect("/index.html");
                return;
            }

            // POST 방식 회원가입 처리
            if (method == HttpMethod.POST && path.equals("/user/signup")) {
                Map<String, String> params = request.getBodyParams();
                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );
                MemoryUserRepository.getInstance().addUser(user);
                response.redirect("/index.html");  // 쿠키 설정 없음
                return;
            }

            // 로그인 처리
            if (method == HttpMethod.POST && path.equals("/user/login")) {
                Map<String, String> params = request.getBodyParams();
                User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

                if (user != null && user.getPassword().equals(params.get("password"))) {
                    response.setCookie("login=true");  // 성공 시 쿠키 설정
                    response.redirect("/index.html");
                } else {
                    response.setCookie("login=; Max-Age=0; Path=/");  // 실패 시 쿠키 삭제
                    response.redirect("/user/login_failed.html");
                }
                return;
            }

            // 사용자 목록 요청 (로그인 체크)
            if (path.equals("/user/list.html")) {
                String cookie = request.getHeader("Cookie");

                if (cookie != null && cookie.contains("login=true")) {
                    response.forward("/user/list.html");  // 로그인 상태 유지
                } else {
                    response.redirect("/user/login.html");  // 인증 안 된 경우 로그인 페이지
                }
                return;
            }

            // CSS 파일 요청
            if (path.endsWith(".css")) {
                response.forwardCss(path);
                return;
            }

            // 기본 HTML 요청 처리
            response.forward(path);

        } catch (IOException e) {
            log.log(Level.SEVERE, "응답 중 예외 발생: " + e.getMessage());
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
