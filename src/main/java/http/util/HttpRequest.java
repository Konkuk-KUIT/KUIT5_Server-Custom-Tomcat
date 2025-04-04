package http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String userId;
    private String password;
    private String name;
    private String email;
    private HttpMethod method;
    private String path;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> bodyParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    // 생성자 (회원가입 요청용)
    public HttpRequest(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    // GET 방식 요청 파싱용
    public static HttpRequest from(String queryString) {
        Map<String, String> params = parseQueryParams(queryString);
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");

        return new HttpRequest(userId, password, name, email);
    }

    // 전체 HTTP 요청 메시지를 BufferedReader로부터 읽어 파싱
    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest request = new HttpRequest(null, null, null, null);

        // 요청 라인
        String line = br.readLine();
        System.out.println("[Start Line] " + line); // 요청 라인 로그

        if (line == null) return request;
        String[] requestLineParts = line.split(" ");
        request.method = HttpMethod.valueOf(requestLineParts[0]);
        request.path = requestLineParts[1].split("\\?")[0];
        if (requestLineParts[1].contains("?")) {
            request.queryParams = parseQueryParams(requestLineParts[1].split("\\?")[1]);
        }

        // 헤더 파싱
        int contentLength = 0;
        while (!(line = br.readLine()).equals("")) {
            System.out.println("[Header Line] " + line); // 헤더 로그

            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                request.headers.put(headerParts[0], headerParts[1]);
                if (headerParts[0].equals("Content-Length")) {
                    contentLength = Integer.parseInt(headerParts[1]);
                }
            }
        }

        // 바디 읽기
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            String body = new String(bodyChars);
            System.out.println("[Body] " + body); //바디 로그
            request.bodyParams = parseQueryParams(body);
        }

        return request;
    }

    private static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getParameter(String key) {
        switch (key) {
            case "userId": return userId;
            case "password": return password;
            case "name": return name;
            case "email": return email;
            default: return null;
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }


}
