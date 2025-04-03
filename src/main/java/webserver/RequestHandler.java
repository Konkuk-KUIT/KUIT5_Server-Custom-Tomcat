package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {

    String WEBAPP = "/Users/jonnykim/KUITMission/KUIT5_Server-Custom-Tomcat/webapp";
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


            String requestFirstLine = getRequestFirstLine(br);
            byte[] body = requestFirstLine.getBytes();

            StringTokenizer st = new StringTokenizer(requestFirstLine, " ");
            String method = st.nextToken();
            String url = st.nextToken();

            //요구사항 1번
            if (url.equals("/")) {
                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);
            }

            if (url.contains("html")) {
                filePath = Paths.get(WEBAPP + url);
                body = Files.readAllBytes(filePath);
            }

            //요구사항 2번
            if (url.contains("signup") & method.equals("GET")) {
                String[] str = url.split("\\?");
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(str[1]);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);
            }

            // 요구사항 3번
            if (url.contains("signup") & method.equals("POST")) {
                String requestBody = getRequestBody(br);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                MemoryUserRepository.getInstance().addUser(user);

                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);

                //요구사항 4번
                String redirectUrl = "/../index.html";
                response302Header(dos, redirectUrl);
            }

            // 요구사항 5번
            if (url.contains("login") & method.equals("POST")) {
                String requestBody = getRequestBody(br);
                Map<String, String> map = HttpRequestUtils.parseQueryParameter(requestBody);
                User user = MemoryUserRepository.getInstance().findUserById(map.get("userId"));


                if (user==null||!user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(WEBAPP + "/user/login_failed.html");
                    body = Files.readAllBytes(filePath);

                    String redirectUrl = "/user/login_failed.html";
                    response302Header(dos, redirectUrl);
                }
                if (user.getPassword().equals(map.get("password"))) {
                    filePath = Paths.get(WEBAPP + "/index.html");
                    body = Files.readAllBytes(filePath);
                    String redirectUrl = "/index.html";
                    System.out.println("실행");
                    loginResponse302Header(dos, redirectUrl);
                }
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

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
            dos.writeBytes("Set-Cookie: logined=true;"+ "Path=/;"+"\r\n");
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


}
