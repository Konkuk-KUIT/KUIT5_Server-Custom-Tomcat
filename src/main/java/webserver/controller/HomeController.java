package webserver.controller;

import constant.URL;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(URL.HOME_URL.getUrl());
    }
}
