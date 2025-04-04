package http.util;
//HTTP 응답 상태 코드를 enum으로 정의

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String description;

    HttpStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}