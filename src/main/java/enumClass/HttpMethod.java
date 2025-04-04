package enumClass;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS,
    HEAD;

    public static HttpMethod fromString(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
