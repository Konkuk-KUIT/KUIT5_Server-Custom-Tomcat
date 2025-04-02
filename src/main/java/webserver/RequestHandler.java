package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
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
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            System.out.println("요청 라인: " + line);
            if (line == null || line.isEmpty()) {
                System.out.println("요청이 null 또는 비어 있음 - 응답 없이 종료됨");
                return;
            }

            String[] tokens = line.split(" ");
            String method = tokens[0];
            String url = tokens[1];

/*            // POST 요청일 때만 body 읽기
            int contentLength = 0;
            if (method.equals("POST")) {
                // 헤더 파싱
                String headerLine;
                while (!(headerLine = br.readLine()).equals("")) {
                    if (headerLine.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                    }
                }
                // 본문 읽기
                String body = IOUtils.readData(br, contentLength);
                System.out.println("Body 내용: " + body);
            }*/

            if (url.equals("/")) {
                url = "/index.html";
            }

            // URL 라우팅 처리
            if (url.equals("/user/userList")) {
                url = "/user/list.html";
            }

            if (url.equals("/user/login")) {
                url = "/user/login.html";
            }


            ///요구사항 2: get으로 회원가입 쿼리스트링처리
            if (method.equals("GET")&&url.startsWith("/user/signup")) {
                String queryString = url.split("\\?")[1];

                // 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                MemoryUserRepository.getInstance().addUser(user);
                System.out.println("회원가입 성공: " + user.getUserId());

                response302Header(dos, "/index.html");
                return;
            }

            /// 요구사항 3: POST 방식으로 회원가입
            int contentLengthSignin = 0;
            if (method.equals("POST")&&url.startsWith("/user/signup")) {
                // 헤더 파싱
                String headerLine;
                while (!(headerLine = br.readLine()).equals("")) {
                    if (headerLine.startsWith("Content-Length:")) {
                        contentLengthSignin = Integer.parseInt(headerLine.split(":")[1].trim());
                    }
                }
                // 본문 읽기
                String body = IOUtils.readData(br, contentLengthSignin);
                System.out.println("Post_Body 내용: " + body);

                // 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                MemoryUserRepository.getInstance().addUser(user);
                System.out.println("POST 회원가입 성공: " + user.getUserId());

                /// 요구사항 4: 302 status code 적용
                response302Header(dos, "/index.html");
                return;
            }







            String filePath = "../../../webapp" + url; ////흠.... 경로가......
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
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    /// 요구사항 4: 302 status code 적용
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
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

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }}