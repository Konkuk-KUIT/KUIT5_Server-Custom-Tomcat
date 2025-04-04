package http.util.httpResponse;

import enumClass.ContentType;
import enumClass.CookieConstants;
import enumClass.HttpStatus;

import java.io.*;
import java.nio.file.Files;

import static enumClass.ContentType.*;
import static enumClass.CookieConstants.*;
import static enumClass.HttpStatus.*;

public class HttpResponse {

    private HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public static HttpResponse from(DataOutputStream dos){
        return new HttpResponse(dos);
    }

    private final String ROOT = "webapp";

    private final DataOutputStream dos;
    private byte[] body;

    StringBuilder stringBuilder = new StringBuilder();

    public void send200Css(String resource) throws IOException {
        addStartLine200();
        addHeaderContent(resource, CSS);
        setBody(resource);
        flush();
    }

    public void send200Html(String resource) throws IOException {
        addStartLine200();
        addHeaderContent(resource, HTML);
        setBody(resource);
        flush();
    }

    public void send302(String location) throws IOException {
        addStartLine302();
        addHeaderRedirectLocation(location);
        flush();
    }

    public void send302WithLoginCookie(String location) throws IOException {
        addStartLine302();
        addHeaderLoginCookie();
        addHeaderRedirectLocation(location);
        flush();
    }


    public void setBody(String resource) throws IOException {
        File file = new File(ROOT + resource);
        body = Files.readAllBytes(file.toPath());
    }

    private int getContentLength(String resource) throws IOException {
        File file = new File(ROOT + resource);
        return Files.readAllBytes(file.toPath()).length;
    }

    public void addStartLine200(){
        stringBuilder.append(OK.getStatusLine());
    }

    public void addStartLine302(){
        stringBuilder.append(FOUND.getStatusLine());
    }

    public void addHeaderContent(String resource, ContentType contentType) throws IOException {
        int contentLength = getContentLength(resource);
        stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
        stringBuilder.append("Content-Type: " + contentType.getValue() + "\r\n");
        stringBuilder.append("Content-Length: " + contentLength + "\r\n");
        stringBuilder.append("\r\n");
    }

    public void addHeaderRedirectLocation(String redirectLocation){
        stringBuilder.append("Location: " + redirectLocation + "\r\n");
        stringBuilder.append("\r\n");
    }

    public void addHeaderLoginCookie(){
        stringBuilder.append(LOGIN_COOKIE.getValue());
    }

    private void flush() throws IOException {
        dos.writeBytes(stringBuilder.toString());
        dos.write(body);
        dos.flush();
    }


}
