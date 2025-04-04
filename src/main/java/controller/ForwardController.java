package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            response.forward(request.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}