package http.util;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String key;

    HttpHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}