package controller;

import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;

import java.io.IOException;

public class CssController implements Controller{

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.send200Css(httpRequest.getPath());
    }
}
