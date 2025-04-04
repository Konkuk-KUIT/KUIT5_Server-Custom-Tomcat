package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {

    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public static HttpResponse from(DataOutputStream dos) {
        return new HttpResponse(dos);
    }

    public void response302Header(String redirectUrl) throws IOException {
        dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
        dos.writeBytes("Location: " + redirectUrl + "\r\n");
        dos.writeBytes("\r\n");

        dos.flush();

    }

    public void loginResponse302Header(String redirectUrl) throws IOException {

        dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
        dos.writeBytes("Location: " + redirectUrl + "\r\n");
        dos.writeBytes("Set-Cookie: logined=true;" + "Path=/;" + "\r\n");
        dos.writeBytes("\r\n");

        dos.flush();
    }

    public void response200Header(int lengthOfBodyContent, String contentType) throws IOException {

        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");

    }

    public void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
