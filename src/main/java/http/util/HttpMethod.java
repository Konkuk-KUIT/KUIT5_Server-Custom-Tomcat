package http.util;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod from(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
