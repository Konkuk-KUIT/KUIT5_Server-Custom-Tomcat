package http.model;

import http.enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    // 정적 파일을 응답으로 전송
    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
        headers.put("Content-Type", getContentType(path));
        headers.put("Content-Length", String.valueOf(body.length));

        writeStatusLine(HttpStatus.OK);
        writeHeaders();
        writeBody(body);
    }

    // 리다이렉트 응답 전송
    public void sendRedirect(String path) throws IOException {
        writeStatusLine(HttpStatus.FOUND);
        dos.writeBytes("Location: " + path + "\r\n");
        dos.writeBytes("\r\n");
    }

    // 리다이렉트 + 쿠키 전송
    public void sendRedirectWithCookie(String path, String cookie) throws IOException {
        writeStatusLine(HttpStatus.FOUND);
        dos.writeBytes("Location: " + path + "\r\n");
        dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void writeStatusLine(HttpStatus status) throws IOException {
        dos.writeBytes(status.getStatusLine() + "\r\n");
    }

    private void writeHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void writeBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
