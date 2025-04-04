package model;

import enums.StatusCode;

public class HttpResponseStartLine {
    private String version = "HTTP/1.1";
    private String statusCode;
    private String statusMessage;

    public HttpResponseStartLine(StatusCode code){
        statusCode = String.valueOf(code.getCode());
        statusMessage = code.toString();
    }

    @Override
    public String toString() {
        return version + " " + statusCode + " " + statusMessage;
    }
}
