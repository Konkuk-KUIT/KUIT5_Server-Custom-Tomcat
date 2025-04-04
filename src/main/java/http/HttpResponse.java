package http;

import http.httpEnum.HttpHeader;
import http.httpEnum.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class HttpResponse {
    private final DataOutputStream dos;
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));

        dos.writeBytes(HttpStatus.OK.getStatusLine());
        dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/html;charset=utf-8"));
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(body.length)));
        dos.writeBytes("\r\n");
        dos.write(body);
        dos.flush();
    }

    public void forwardCss(String path) throws IOException {
        log.info("css 파일 요청 들어옴");
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));

        dos.writeBytes(HttpStatus.OK.getStatusLine());
        dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/css;charset=utf-8"));
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(body.length)));
        dos.writeBytes("\r\n");
        dos.write(body);
        dos.flush();
        log.info("css 전송 성공");
    }

    public void redirect(String path) throws IOException {
        dos.writeBytes(HttpStatus.FOUND.getStatusLine());
        dos.writeBytes(HttpHeader.LOCATION.format(path));
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void redirect(String path, String cookie) throws IOException { // 쿠키 있을 때 오버로딩
        dos.writeBytes(HttpStatus.FOUND.getStatusLine());
        dos.writeBytes(HttpHeader.LOCATION.format(path));
        dos.writeBytes(HttpHeader.SET_COOKIE.format(cookie));
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void notFound() throws IOException {
        String body = "<h1>404 Not Found</h1>";
        dos.writeBytes(HttpStatus.NOT_FOUND.getStatusLine());
        dos.writeBytes(HttpHeader.CONTENT_TYPE.format("text/html;charset=utf-8"));
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.format(String.valueOf(body.getBytes().length)));
        dos.writeBytes("\r\n");
        dos.write(body.getBytes());
        dos.flush();
    }
}
