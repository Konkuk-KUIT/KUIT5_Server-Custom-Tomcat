package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(String method, String path, String version,
                        Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        String[] lineSplit = requestLine.split(" ");
        String method = lineSplit[0];
        String path = lineSplit[1];
        String version = lineSplit[2];

        Map<String, String> headers = new HashMap<>(); // 헤더 파싱, 키 값을 찾는 것을 더 효율적으로 하기 위해 Map 사용
        int contentLength = 0;

        String line;
        while (!(line = br.readLine()).equals("")) {
            String[] headerTokens = line.split(": ", 2);
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0], headerTokens[1]);
            }

            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(headerTokens[1]);
            }
        }

        String body = null;
        if (contentLength > 0) { // 바디 내용이 존재한다면 읽음
            char[] bodyData = new char[contentLength];
            br.read(bodyData, 0, contentLength);
            body = new String(bodyData);
        }

        return new HttpRequest(method, path, version, headers, body);
    }


    // Getter들 필요에 따라 추가
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getVersion() { return version; }
    public String getHeader(String key) { return headers.get(key); }
    public Map<String, String> getHeaderLine(){ return headers; }
    public String getBody() { return body; }
}

