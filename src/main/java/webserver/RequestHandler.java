package webserver;

import com.sun.net.httpserver.HttpContext;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;
import webserver.controller.ForwardController;
import webserver.controller.HomeController;
import webserver.controller.HttpController;
import webserver.controller.SignUpController;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webserver.ContentType.*;
import static webserver.HttpMethod.*;
import static webserver.URL.*;


public class RequestHandler implements Runnable {

    String WEBAPP = URL.WEBAPP.getPath();
    Path filePath;

    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private HttpController controller = new ForwardController();

    public RequestHandler(Socket connection){
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);


            byte[] body = null;


            String contentType = null;

            //요구사항 1번
            if (httpRequest.getUrl().equals(ROOT.getPath())) {
                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);
                httpResponse.response302Header("/index.html");
                contentType = "text/html";

//                controller = new HomeController();
            }

            if (httpRequest.getMethod().equals("GET")&&httpRequest.getUrl().endsWith(HTML.getValue())) {
                filePath = Paths.get(WEBAPP + httpRequest.getUrl());
                body = Files.readAllBytes(filePath);
                contentType = "text/html";

//                controller = new ForwardController();
            }


            //요구사항 2번
            if (httpRequest.getUrl().contains("signup") & httpRequest.getMethod().equals(GET.getMethod())) {
                String[] str = httpRequest.getUrl().split("\\?");
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(str[1]);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                contentType = "text/html";
                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);

//                controller = new SignUpController();
            }

            // 요구사항 3번
            if (httpRequest.getUrl().contains("signup") & httpRequest.getMethod().equals(POST.getMethod())) {
                String requestBody = httpRequest.getRequestBody();
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);

                //요구사항 4번
                httpResponse.response302Header("/index.html");

//                controller = new SignUpController();
            }

/*            // 2,3,4 번 통합
            if(httpRequest.getUrl().contains("signup")){
                controller = new SignUpController();
            }*/

            // 요구사항 5번
            if (httpRequest.getUrl().contains("login") & httpRequest.getMethod().equals(POST.getMethod())) {
                String requestBody = httpRequest.getRequestBody();
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = MemoryUserRepository.getInstance().findUserById(map.get("userId"));


                if (user == null || !user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(LOGIN_FAILED_HTML.getPath());
                    body = Files.readAllBytes(filePath);

                    String redirectUrl = "/user/login_failed.html";
                    httpResponse.response302Header(redirectUrl);
                }
                if (user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(INDEX_HTML.getPath());
                    body = Files.readAllBytes(filePath);
                    String redirectUrl = "/index.html";
                    httpResponse.loginResponse302Header(redirectUrl);
                }
            }

            // 요구사항 6번
            if (httpRequest.getUrl().contains("userList")) {
                String cookieQuery = httpRequest.getCookieInfo();
                Map<String, String> cookie = HttpRequestUtils.parseQueryParameter(cookieQuery);
                if (cookieQuery == null || !cookie.get("logined").equals("true")) {
                    filePath = Paths.get(LOGIN_HTML.getPath());
                    String redirectUrl = "/user/login.html";
                    httpResponse.response302Header(redirectUrl);

                    body = Files.readAllBytes(filePath);
                }
                if (cookie.get("logined").equals("true")) {
                    filePath = Paths.get(LIST_HTML.getPath());
                    body = Files.readAllBytes(filePath);
                    String redirectUrl = "/list.html";
                }
            }

            // 요구사항 7번
            if (httpRequest.getUrl().endsWith(ContentType.CSS.getValue())) {
                contentType = "text/css";
                filePath = Paths.get(STYLE_CSS.getPath());
                body = Files.readAllBytes(filePath);
            }
            if (httpRequest.getUrl().endsWith(ContentType.JS.getValue())) {
                contentType = "text/javascript";
                filePath = Paths.get(WEBAPP + "/js/script.js");
                body = Files.readAllBytes(filePath);
            }

            httpResponse.response200Header(body.length, contentType);
            httpResponse.responseBody(body);

//            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


}
