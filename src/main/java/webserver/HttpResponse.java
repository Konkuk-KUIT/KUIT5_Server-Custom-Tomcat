package webserver;

import constant.HttpHeader;
import constant.HttpStatus;
import constant.URL;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class HttpResponse {
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos)  {
        this.dos = dos;
    }

    public void forward(String path) throws IOException {
        File file = new File(URL.ROOT.getUrl() + path);

        if(file.exists()) {
            byte[] body = Files.readAllBytes(file.toPath());
            String contentType = getContentType(path);
            writeResponseHeader(HttpStatus.OK.getStatusLine(), contentType, body.length);
            dos.write(body);
            dos.flush();
        } else {
            notFound();
        }
    }

    public void notFound() throws IOException {
        String msg = "<h1>404 Not Found</h1>";

        writeResponseHeader(HttpStatus.NOT_FOUND.getStatusLine(), "text/html;", msg.length());
        dos.writeBytes(msg);
        dos.flush();
    }

    public void redirect(String locationUrl, String cookieValue) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + locationUrl + "\r\n");
        if(cookieValue != null) {
            dos.writeBytes(HttpHeader.SET_COOKIE.getHeaderWithValue(cookieValue));
        }
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void writeResponseHeader(String status, String contentType, int contentLength) throws IOException {
        dos.writeBytes("HTTP/1.1 " + status + "\r\n");
        dos.writeBytes("Content-Type: " + contentType + "; charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + contentLength + "\r\n");
        dos.writeBytes("\r\n");
    }

    private static String getContentType(String path) {
        if(path.endsWith(".html")) return "text/html";
        if(path.endsWith(".css")) return "text/css";
        if(path.endsWith(".js")) return "application/javascript";
        return "application/octet-stream";
    }
}
