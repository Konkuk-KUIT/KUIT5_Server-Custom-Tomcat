package webserver;

import java.util.PrimitiveIterator;

public class HttpStartLine {
    private final String method;
    private final String path;
    private final String query;
    private final String version;

    private HttpStartLine(String method, String path, String version) {
        this.method = method;
        String[] splitedPath = path.split("\\?");
        this.path = splitedPath[0];
        this.query = splitedPath.length > 1 ? splitedPath[1] :  "";
        this.version = version;
    }

    public static HttpStartLine parse(String requestLine) {
        String[] parts = requestLine.split(" ");
        return new HttpStartLine(parts[0], parts[1], parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
