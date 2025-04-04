package enumClass;

public enum HttpStatus {
    OK("HTTP/1.1 200 OK\r\n"),
    FOUND("HTTP/1.1 302 Found\r\n");

    private final String statusLine;

    HttpStatus(String statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLine() {
        return statusLine;
    }
}
