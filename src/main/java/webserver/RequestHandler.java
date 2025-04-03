package webserver;

import http.util.HttpRequestUtils;
import model.User;
import db.MemoryUserRepository;
import http.util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            log.info("Request Line: " + requestLine);
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            // 헤더 파싱
            boolean isLogined = false;
            String line;
            int contentLength = 0;

            Map<String, String> headers = new HashMap<>();
            while (!(line = br.readLine()).equals("")) {
                int idx = line.indexOf(": ");
                if (idx != -1) {
                    headers.put(line.substring(0, idx), line.substring(idx + 2));
                }

                if (line.startsWith("Cookie") && line.contains("logined=true")) {
                    isLogined = true;
                }

                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            // 유저 리스트 출력하기
            if (url.equals("/user/userList")) {
                if (isLogined) {
                    String filePath = "./webapp/user/list.html";
                    if (Files.exists(Paths.get(filePath))) {
                        byte[] body = Files.readAllBytes(Paths.get(filePath));
                        //System.out.println("파일 경로: " + Paths.get("./webapp/user/list.html").toAbsolutePath());
                        response200Header(dos, body.length, "text/html;charset=utf-8");
                        responseBody(dos, body);
                    } else {
                        byte[] body = "<h1>404 Not Found</h1>".getBytes();
                        response404Header(dos, body.length);
                        responseBody(dos, body);
                    }
                } else {
                    response302Header(dos, "/user/login.html");
                }
                return;
            }

            // 로그인하기
            if (method.equals("POST") && url.equals("/user/login")) {
                contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
                String body = IOUtils.readData(br, contentLength);

                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                String userId = params.get("userId");
                String password = params.get("password");

                User user = MemoryUserRepository.getInstance().findUserById(userId);

                if (user != null && user.getPassword().equals(password)) {
                    response302LoginSuccess(dos, "/index.html");
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }
                return;
            }

            // POST 방식 회원가입 처리
            if (method.equals("POST") && url.equals("/user/signup")) {
                // body 읽기
                String body = IOUtils.readData(br, contentLength);

                // 파라미터 파싱 및 유저 등록
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );
                MemoryUserRepository.getInstance().addUser(user);

                response302Header(dos, "/index.html");
                return;
            }

            // GET 방식 회원가입
            if (url.startsWith("/user/signup")) {
                int idx = url.indexOf("?");
                if (idx != -1) {
                    String queryString = url.substring(idx + 1);
                    Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

                    User user = new User(
                            params.get("userId"),
                            params.get("password"),
                            params.get("name"),
                            params.get("email")
                    );

                    MemoryUserRepository.getInstance().addUser(user);
                }

                response302Header(dos, "/index.html");
                return;
            }

            // 인덱스 페이지
            if (url.equals("/")) {
                url = "/index.html";
            }

            String filePath = "./webapp" + url;

            if (Files.exists(Paths.get(filePath))) {
                byte[] body = Files.readAllBytes(Paths.get(filePath));

                String contentType = "text/html;charset=utf-8";
                if (filePath.endsWith(".css")) {
                    contentType = "text/css";
                }

                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            } else {
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302LoginSuccess(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
