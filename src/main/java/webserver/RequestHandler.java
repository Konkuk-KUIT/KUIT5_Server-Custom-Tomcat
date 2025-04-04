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

            HttpRequest httpRequest = HttpRequest.from(br);

            DataOutputStream dos = new DataOutputStream(out);


            String requestFirstLine = getRequestFirstLine(br);
            byte[] body = requestFirstLine.getBytes();

            StringTokenizer st = new StringTokenizer(requestFirstLine, " ");
            String method = st.nextToken();
            String url = st.nextToken();

            String contentType = null;

            //요구사항 1번
            if (url.equals(ROOT.getPath())) {
                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);
                response302Header(dos, "/index.html");

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
                String requestBody = getRequestBody(br);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(INDEX_HTML.getPath());
                body = Files.readAllBytes(filePath);

                //요구사항 4번
                String redirectUrl = "/index.html";
                response302Header(dos, redirectUrl);
            }

            // 요구사항 5번
            if (url.contains("login") & method.equals(POST.getMethod())) {
                String requestBody = getRequestBody(br);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = MemoryUserRepository.getInstance().findUserById(map.get("userId"));


                if (user == null || !user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(LOGIN_FAILED_HTML.getPath());
                    body = Files.readAllBytes(filePath);

                    String redirectUrl = "/user/login_failed.html";
                    response302Header(dos, redirectUrl);
                }
                if (user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(INDEX_HTML.getPath());
                    body = Files.readAllBytes(filePath);
                    String redirectUrl = "/index.html";
                    loginResponse302Header(dos, redirectUrl);
                }
            }

            // 요구사항 6번
            if (url.contains("userList")) {
                String cookieQuery = getCookieInfo(br);
                Map<String, String> cookie = HttpRequestUtils.parseQueryParameter(cookieQuery);
                if (cookieQuery == null || !cookie.get("logined").equals("true")) {
                    filePath = Paths.get(LOGIN_HTML.getPath());
                    String redirectUrl = "/user/login.html";
                    response302Header(dos, redirectUrl);

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

            response200Header(dos, body.length, contentType);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
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

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");

            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void loginResponse302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true;" + "Path=/;" + "\r\n");
            dos.writeBytes("\r\n");

            dos.flush();
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

    private String getRequestFirstLine(BufferedReader br) throws IOException {
        try {
            return br.readLine();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    private String getRequestBody(BufferedReader br) throws IOException {
        try {
            int requestContentLength = 0;
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }
            return IOUtils.readData(br, requestContentLength);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    private String getCookieInfo(BufferedReader br) throws IOException {
        try {
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                if (line.startsWith("Cookie")) {
                    return line.split(": ")[1];
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }


}
