package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface HttpController {


    public void execute(HttpRequest request, HttpResponse response) throws IOException;


}
