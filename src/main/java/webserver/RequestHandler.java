package webserver;

import db.MemoryUserRepository;
import http.*;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
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
            HttpResponse response = new HttpResponse(out);
            HttpRequest request = HttpRequest.from(br);

            HttpMethod method = HttpMethod.from(request.getMethod());
            String url = request.getPath();
            Map<String, String> headers = request.getHeaderLine();

            if (url.equals(UrlPath.ROOT.getPath())) {
                url = UrlPath.INDEX.getPath();
            }

            UrlPath matchedPath = UrlPath.from(url);

            switch (matchedPath) {
                case SIGNUP:
                    if (method == HttpMethod.POST) {
                        log.info("회원가입 선택");
                        handleSignUp(request.getBody(), response); // 바디가 이미 정리된 상태이므로, String 값을 바로 넘겨줌.
                        return;
                    }
                    break;

                case LOGIN:
                    if (method == HttpMethod.POST) {
                        log.info("로그인 선택");
                        handleLogin(request.getBody(), response);
                        return;
                    }
                    break;

                case USER_LIST:
                    if (!isLoggedIn(headers)) {
                        log.info("로그인하지 않은 사용자의 userList 접근");
                        response.redirect(UrlPath.LOGIN_HTML.getPath());
                    } else {
                        log.info("로그인한 사용자의 userList 접근");
                        response.redirect(UrlPath.USER_LIST_HTML.getPath());
                    }
                    return;

                default:
                    if(!isValidPath(url)){
                        response.notFound();
                        return;
                    }
                    if (url.endsWith(".css")) {
                        response.forwardCss(url);
                        return;
                    }
                    response.forward(url);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private static boolean isValidPath(String url) throws IOException {
        String filePath = "./webapp" + url;
        File file = new File(filePath);

        if (file.exists()) {
            return true;
        }

        return false;
    }

    private void handleLogin(String body, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
        User user = MemoryUserRepository.getInstance().findUserById(params.get(UserQueryKey.PASSWORD.getKey()));

        if (user == null || !user.getPassword().equals(params.get(UserQueryKey.PASSWORD.getKey()))) {
            log.info("user login failed");
            response.redirect(UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        log.info("user login success!: " + user.getUserId());
        response.redirect(UrlPath.INDEX.getPath(), "logined=true");
    }

    private void handleSignUp(String body, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
        User user = new User(
                params.get(UserQueryKey.USER_ID.getKey()),
                params.get(UserQueryKey.PASSWORD.getKey()),
                params.get(UserQueryKey.NAME.getKey()),
                params.get(UserQueryKey.EMAIL.getKey())
        );
        MemoryUserRepository.getInstance().addUser(user);
        log.info("New user signed up: " + user.getUserId());

        response.redirect(UrlPath.INDEX.getPath());
    }

    private boolean isLoggedIn(Map<String, String> headers) {
        String cookieLine = headers.get("Cookie");
        if (cookieLine == null) {
            return false;
        }

        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieLine);
        return "true".equals(cookies.get("logined"));
    }
}
