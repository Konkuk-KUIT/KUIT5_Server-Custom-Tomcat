package http.util;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String statusLine;

    HttpStatus(String statusLine) {
        this.statusLine = statusLine;
    }

    @Override
    public String toString() {
        return statusLine;
    }
}

