package constant;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found", true),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reason;
    private final boolean isRedirect;

    HttpStatus(int code, String reason) {
        this(code, reason, false);
    }

    HttpStatus(int code, String reason, boolean isRedirect) {
        this.code = code;
        this.reason = reason;
        this.isRedirect = isRedirect;
    }

    public String getStatusLine() {
        return code + " " + reason;
    }

    public boolean isRedirect() {
        return isRedirect;
    }
}

