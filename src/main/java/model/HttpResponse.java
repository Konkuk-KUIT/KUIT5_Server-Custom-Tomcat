package model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpHeader.*;
import static enums.StatusCode.*;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private static final String WEB_ROOT = "webapp";
    private final DataOutputStream dos;

    private HttpResponseStartLine startLine;
    private Map<String, String> headers;
    private byte[] body = null;

    public HttpResponse(DataOutputStream dos) throws IOException {
        this.dos = dos;
    }

    public void redirect(String redirectUrl, boolean logined) throws IOException {
        set302Header(redirectUrl, logined);
        body = null;
        writeResponse();
    }

    public void forward(String filePath){
        String contentType = "html";
        if(filePath.endsWith(".css")){
            contentType = "css";
        }
        try {
            Path path = Paths.get(WEB_ROOT, filePath);
            if (Files.exists(path) && !Files.isDirectory(path)) {
                body = Files.readAllBytes(path);
                set200Header(body.length, contentType);
            } else {
                body = null;
                set404Header();
            }
            writeResponse();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error serving file: " + filePath, e);
        }
    }

    private void set200Header(int lengthOfBodyContent, String contentType) {
        startLine = new HttpResponseStartLine(OK);
        Map<String, String> header200 = new HashMap<>();
        header200.put(CONTENT_TYPE.getValue(), "text/" + contentType + ";charset=utf-8");
        header200.put(CONTENT_LENGTH.getValue(), String.valueOf(lengthOfBodyContent));
        headers = header200;
    }

    private void set302Header(String redirectUrl, boolean logined) {
        startLine = new HttpResponseStartLine(FOUND);
        Map<String, String> header302 = new HashMap<>();
        header302.put(LOCATION.getValue(), redirectUrl);
        if(logined) {
            header302.put(SET_COOKIE.getValue(), "logined=true; Path=/; HttpOnly");
        }
        headers = header302;
    }

    private void set404Header() {
        startLine = new HttpResponseStartLine(NOT_FOUND);
        Map<String, String> header404 = new HashMap<>();
        header404.put(CONTENT_TYPE.getValue(), "text/html;charset=utf-8");
        header404.put(CONTENT_LENGTH.getValue(), "0");
        headers = header404;
    }

    private void writeResponse() throws IOException {
        try {
            dos.writeBytes( startLine+"\r\n");
            for(String header: headers.keySet()){
                dos.writeBytes(header+": "+headers.get(header)+"\r\n");
            }
            dos.writeBytes("\r\n");
            if(body != null){
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
