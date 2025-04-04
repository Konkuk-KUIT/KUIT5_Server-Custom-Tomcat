package data;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    DEFAULT("application/octet-stream");

    ContentType(String ignoredType) {
    }


    public static ContentType fromPath(String path) {
        if (path.endsWith(".html")) return HTML;
        if (path.endsWith(".css")) return CSS;
        if (path.endsWith(".js")) return JS;
        return DEFAULT;
    }

}
