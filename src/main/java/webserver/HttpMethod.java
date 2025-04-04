package webserver;

public enum HttpMethod {
    GET("get"), POST("post"), PUT("put"), DELETE("delete");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
