package controller;

import enumClass.UrlPath;
import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;

import java.io.IOException;

import static enumClass.UrlPath.*;

public class HomeController implements Controller{

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.send200Html(INDEX.getPath());
    }
}
