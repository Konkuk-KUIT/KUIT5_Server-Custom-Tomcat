package model;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestStartLine {
    private String method;
    private URI uri;
    private String version;

    public HttpRequestStartLine(String line) throws URISyntaxException {
        String[] tokens = line.split(" ");

        this.method = tokens[0];
        this.uri = new URI(tokens[1]);
        this.version = tokens[2];
    }

    public String getMethod() {
        return method;
    }
    public URI getURI() {
        return uri;
    }
    public String getVersion() {
        return version;
    }
}
