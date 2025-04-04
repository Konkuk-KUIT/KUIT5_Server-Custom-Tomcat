package http;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getKey() {
        return headerName;
    }

    public String format(String value) {
        return headerName + ": " + value + "\r\n";
    }
}
