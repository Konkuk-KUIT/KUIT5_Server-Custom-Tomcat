package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
