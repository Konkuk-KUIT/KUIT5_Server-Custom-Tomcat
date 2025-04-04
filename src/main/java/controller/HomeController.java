package controller;

import model.HttpRequest;
import model.HttpResponse;

import static enums.URL.INDEX_HTML;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward(INDEX_HTML.getUrl());
    }
}
