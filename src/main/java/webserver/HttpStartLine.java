package webserver;

import Enums.HttpMethod;

public class HttpStartLine {
    private String method;
    private String path;
    private String body;

    public HttpStartLine(String [] httpStartLine){
        this.method = httpStartLine[0];
        this.path = httpStartLine[1];
        this.body = httpStartLine[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
