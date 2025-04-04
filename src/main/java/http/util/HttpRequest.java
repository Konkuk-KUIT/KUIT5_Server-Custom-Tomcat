package http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;
    private final String queryString;

    private HttpRequest(String method, String path, String version, Map<String, String> headers, String body, String queryString) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers.putAll(headers);
        this.body = body;
        this.queryString = queryString;
    }


    public static HttpRequest from(BufferedReader reader) throws IOException {
        // Start Line
        String startLine = reader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid HTTP Request: empty start line");
        }
        String[] parts = startLine.split(" ");
        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        String routePath = path.split("\\?")[0];
        String queryString = "";
        if (path.contains("?")) {
            queryString = path.split("\\?", 2)[1]; // ⭐
        }

        // Header 있을때만
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Body Content-Length 있을때만
        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }

        String body = "";
        if (contentLength > 0) {
            body = IOUtils.readData(reader, contentLength);
        }

        return new HttpRequest(method, path, version, headers, body,queryString);
    }

    public String getMethod() {
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

    public String getBody() {
        return body;
    }

    public String getQueryString() {
        return queryString;
    }
}