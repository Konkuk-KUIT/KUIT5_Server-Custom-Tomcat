package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public class DefaultController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getUrl());
    }
}