package http.enums;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String description;

    HttpStatus(String description) {
        this.description = description;
    }

    public String getStatusLine() {
        return "HTTP/1.1 " + description;
    }
}
