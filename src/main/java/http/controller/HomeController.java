package http.controller;

import http.HttpRequest;
import http.HttpResponse;
import http.httpEnum.UrlPath;

import java.io.IOException;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(UrlPath.INDEX.getPath());
    }
}
