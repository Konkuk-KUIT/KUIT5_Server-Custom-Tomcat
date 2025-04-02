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

public class RequestHandler implements Runnable {
    //    ../../../../webapp/
    String WEBAPP = "/Users/jonnykim/KUITMission/KUIT5_Server-Custom-Tomcat/webapp";
    // 상대위치로 변환
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


            String requestHeader = getRequestHeader(br);
            byte[] body = requestHeader.getBytes();

            StringTokenizer st = new StringTokenizer(requestHeader, " ");
            String method = st.nextToken();
            String url = st.nextToken();

            if(url.equals("/")){
                filePath = Paths.get(WEBAPP + "/index.html");
                body = Files.readAllBytes(filePath);
            }

            if (url.contains("html")) {
                filePath = Paths.get(WEBAPP + url);
                body = Files.readAllBytes(filePath);
            }


//            //요구사항 2번
//            if (info[1].contains("?")) {
//                StringTokenizer st = new StringTokenizer(info[1], "?");
//                st.nextToken();
//                String temp = st.nextToken();
//                System.out.println(temp);
//                Map<String, String> map = HttpRequestUtils.parseQueryParameter(temp);
//                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
//                MemoryUserRepository.getInstance().addUser(user);
//                Paths.get(WEBAPP + "/index.html");
//            } else {
//                //요구사항 1번
//                if(!info[1].contains("html")) {
//                    filePath = Paths.get(WEBAPP + "/index.html");
//                }
//                filePath = Paths.get(WEBAPP + info[1]);
//            }

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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getRequestHeader(BufferedReader br) throws IOException {
        try {
            //            StringTokenizer st = new StringTokenizer(line, " ");
//            String[] info = new String[2];
//            info[0] = st.nextToken();
//            info[1] = st.nextToken();
//            if (info[1].equals("/")) {
//                info[1] = "/index.html";
//            }
//            if(!info[1].contains("html")){
//                return line;
//            }
//            //Todo Refactoring
//            return info;
            return br.readLine();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

//    private void test(BufferedReader br) throws IOException {
//        try {
//            System.out.println(br.readLine());
//        }catch (IOException e){
//
//        }
//    }

}
