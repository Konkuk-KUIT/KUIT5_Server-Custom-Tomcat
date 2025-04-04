package http.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.OutputStream;

public class HttpResponse {
    private final DataOutputStream dos;
    private String cookie;

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);;
    }
    // 쿠키를 응답에 추가
    public void setCookie(String cookie ) {
        this.cookie = cookie;
        System.out.println("쿠키 저장됨: " + cookie);
    }

    public void forward(String path) throws IOException {
        System.out.println("forward() 호출됨: " + path); ///////

        Path resourcePath = Paths.get("webapp" + path);
        if (!Files.exists(resourcePath)) {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
            return;
        }
        byte[] body = Files.readAllBytes(resourcePath);

        dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getDescription() + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getKey() + ": text/html;charset=utf-8\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getKey() + ": " + body.length + "\r\n");

        if (cookie != null) {
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        }

        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void redirect(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getDescription() + "\r\n");
        dos.writeBytes(HttpHeader.LOCATION.getKey() + ": " + location + "\r\n");
        if (cookie != null) {
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        }
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    // CSS 파일 응답
    public void forwardCss(String path) throws IOException {
        Path resourcePath = Paths.get("webapp" + path);
        byte[] body = Files.readAllBytes(resourcePath);

        dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getDescription() + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getKey() + ": text/css\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getKey() + ": " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
