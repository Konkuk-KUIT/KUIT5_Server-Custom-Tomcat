package webserver;

import http.util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
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
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String[] info = getRequestInfo(br);

            filePath = Paths.get(WEBAPP + info[1]);

//            byte[] body = "Hello World".getBytes();
//            test(br);
            byte[] body = Files.readAllBytes(filePath);
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
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

    private String[] getRequestInfo(BufferedReader br) throws IOException {
        try {
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line," ");
            String[] info = new String[2];
            info[0] = st.nextToken();
            info[1] = st.nextToken();
            if(info[1].equals("/")){
                info[1]="/index.html";
            }
            //Todo Refactoring
            System.out.println(info[0]);
            System.out.println(info[1]);
            return info;
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
