package Enums;

public enum ContentType {
    CONTENT_TYPE_HTML("text/html"),
    CONTENT_TYPE_CSS("text/css"),
    CONTENT_TYPE_JS("text/js");

    public final String Type;
    ContentType(String contentType) {
        Type = contentType;
    }

    public String getType() {
        return Type;
    }
}
