package webserver;

import constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(HttpStartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException{
        HttpStartLine startLine = HttpStartLine.parse(br.readLine());
        HttpHeaders headers = new HttpHeaders();
        String line;
        while(!(line = br.readLine()).isEmpty()) {
            headers.addHeader(line);
        }

        String body = null;
        if(headers.containHeader(HttpHeader.CONTENT_LENGTH.getHeader())) {
            int contentLength = Integer.parseInt(headers.getHeader(HttpHeader.CONTENT_LENGTH.getHeader()));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        return new HttpRequest(startLine, headers, body);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getPath();
    }

    public String getBody() {
        return body;
    }
    public boolean containHeader(String key) {
        return headers.containHeader(key);
    }

    public String getHeader(String key) {
        return headers.getHeader(key);
    }
}
