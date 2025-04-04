package controller;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
