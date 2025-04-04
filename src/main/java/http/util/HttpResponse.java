package http.util;

import data.ContentType;
import data.UrlData;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    final DataOutputStream dos;
    ContentType contentType;

    public HttpResponse(DataOutputStream dos) throws IOException {
        this.dos = dos;
    }
    public void forward(String path) throws IOException {
        File file = new File(UrlData.DIRECTORY.getData() +path);
        if (!file.exists()) {
            response404Header();
        }
        contentType = ContentType.fromPath(path);
        byte[] body = Files.readAllBytes(file.toPath());
        response200Header(body);
        System.out.println(contentType);
    }
    private void response200Header(byte[] body) throws IOException {
        String statusLine = "HTTP/1.1 200 OK\r\n";
        String headers =
                "Content-Type: "+contentType+";charset=utf-8\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        dos.write(statusLine.getBytes());
        dos.write(headers.getBytes());
        dos.write(body);
        dos.flush();
    }
    private void response404Header() {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }
    public void cookieRedirect(String path) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/; HttpOnly\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
