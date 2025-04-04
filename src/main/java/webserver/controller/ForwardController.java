package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static webserver.URL.WEBAPP;

public class ForwardController implements HttpController{

    private byte[] body;
    private Path filePath;



    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        filePath = Paths.get(WEBAPP.getPath()+request.getUrl());
        body = Files.readAllBytes(filePath);

        response.response200Header(body.length, "text/html");
        response.responseBody(body);
    }


}
