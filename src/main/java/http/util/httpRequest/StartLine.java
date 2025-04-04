package http.util.httpRequest;

public class StartLine {

    private String method;
    private String path;
    private String version;

    public StartLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
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

    public String getUrl(){
        return path.split("\\?")[0];
    }

    public String getQueryString(){
        String[] parts = path.split("\\?", 2);
        return parts.length == 2 ? parts[1] : "";
    }

}
