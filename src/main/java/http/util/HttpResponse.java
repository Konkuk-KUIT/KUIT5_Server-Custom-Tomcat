package http.util;

import webserver.ContentType;


import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HttpResponse {

    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        String filePath = "./webapp" + path;
        File file = new File(filePath);

        if (file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = getContentType(filePath);
            response200Header(body.length, contentType);
            responseBody(body);
        } else {
            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(body.length);
            responseBody(body);
        }
    }

    public void redirect(String path) throws IOException {
        response302Header(path);
    }

    public void redirectWithCookie(String path, String cookie) throws IOException {
        response302HeaderWithCookie(path, cookie);
    }

    private void response302Header(String path) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
            System.out.println(dos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void response302HeaderWithCookie(String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION + ": " + path + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE + ": " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void response404Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void response200Header(int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE + ": " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getContentType(String filePath) {
        return ContentType.fromFilePath(filePath);
    }
}
