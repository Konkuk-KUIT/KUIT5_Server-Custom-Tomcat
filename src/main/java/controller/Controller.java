package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public interface Controller {

    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
