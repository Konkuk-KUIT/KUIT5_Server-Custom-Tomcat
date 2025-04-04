package http;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String statusText;

    HttpStatus(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusLine() { // Todo 필요하려나?
        return "HTTP/1.1 " + statusText + "\r\n";
    }
}
