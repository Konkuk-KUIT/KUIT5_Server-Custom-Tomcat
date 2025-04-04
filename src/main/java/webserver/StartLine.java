package webserver;

import Enums.StatusCode;
import Enums.StatusText;

public class StartLine {
    private String version;
    private StatusCode statusCode;
    private StatusText message;

    public StartLine(String version, StatusCode statusCode, StatusText message){
        this.version = version;
        this.statusCode= statusCode;
        this.message = message;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public StatusText getMessage() {
        return message;
    }
}
