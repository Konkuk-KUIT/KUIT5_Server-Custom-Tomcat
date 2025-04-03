package webserver;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    DEFAULT("", "application/octet-stream");

    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }
    public static String fromFilePath(String filePath) {
        for (ContentType ct : values()) {
            if (filePath.endsWith(ct.extension)) {
                return ct.mimeType;
            }
        }
        return DEFAULT.mimeType;
    }


}
