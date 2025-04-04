package controller;

import data.UrlData;
import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(UrlData.DEFAULT_URL.getData());
    }
}
