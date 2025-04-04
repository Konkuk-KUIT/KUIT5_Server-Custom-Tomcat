package controller;

import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
