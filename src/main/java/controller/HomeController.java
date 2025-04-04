package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import webserver.Route;

import java.io.IOException;

public class HomeController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            response.forward(Route.INDEX.path());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
