package http.model;

import http.enums.HttpMethod;
import http.enums.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(HttpMethod method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("Empty request line");
        }

        String[] tokens = startLine.split(" ");
        HttpMethod method = HttpMethod.from(tokens[0]);
        String path = tokens[1];
        String version = tokens[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).equals("")) {
            int idx = line.indexOf(": ");
            if (idx != -1) {
                headers.put(line.substring(0, idx), line.substring(idx + 2));
            }
        }

        String body = "";
        String contentLength = headers.get(HttpHeader.CONTENT_LENGTH.getKey());
        if (contentLength != null) {
            int length = Integer.parseInt(contentLength);
            char[] bodyChars = new char[length];
            br.read(bodyChars, 0, length);
            body = String.copyValueOf(bodyChars);
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getBody() {
        return body;
    }
}
