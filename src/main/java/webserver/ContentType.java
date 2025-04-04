package webserver;

public enum ContentType {
    HTML("html"),
    CSS("css"),
    JS("js");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
