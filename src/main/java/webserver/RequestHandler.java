package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

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

    public RequestHandler(Socket connection) {
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



            String requestFirstLine = httpRequest.getRequestFirstLine();
            byte[] body = requestFirstLine.getBytes();

            StringTokenizer st = new StringTokenizer(requestFirstLine, " ");
            String method = st.nextToken();
            String url = st.nextToken();

            String contentType = null;

            //요구사항 1번
            if (url.equals(ROOT.getPath())) {
                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);
                httpResponse.response302Header("/index.html");

            }

            if (url.endsWith(HTML.getValue())) {
                filePath = Paths.get(WEBAPP + url);
                body = Files.readAllBytes(filePath);
                contentType = "text/html";
            }

            //요구사항 2번
            if (url.contains("signup") & method.equals(GET.getMethod())) {
                String[] str = url.split("\\?");
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(str[1]);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);
            }

            // 요구사항 3번
            if (url.contains("signup") & method.equals(POST.getMethod())) {
                String requestBody = httpRequest.getRequestBody();
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);

                //요구사항 4번
                httpResponse.response302Header("/index.html");
            }

            // 요구사항 5번
            if (url.contains("login") & method.equals(POST.getMethod())) {
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
            if (url.contains("userList")) {
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
            if (url.endsWith(ContentType.CSS.getValue())) {
                contentType = "text/css";
                filePath = Paths.get(STYLE_CSS.getPath());
                body = Files.readAllBytes(filePath);
            }
            if (url.endsWith(ContentType.JS.getValue())) {
                contentType = "text/javascript";
                filePath = Paths.get(WEBAPP + "/js/script.js");
                body = Files.readAllBytes(filePath);
            }

            httpResponse.response200Header(body.length, contentType);
            httpResponse.responseBody(body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
















}
