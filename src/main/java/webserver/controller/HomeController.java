package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static webserver.URL.INDEX_HTML;

public class HomeController implements HttpController {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    private final byte[] body;
    private final Path filePath = Paths.get(INDEX_HTML.getPath());


    public HomeController() throws IOException {
        body = Files.readAllBytes(filePath);
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        this.httpRequest = request;
        this.httpResponse = response;

        httpResponse.response302Header("/index.html");
        String contentType = "text/html";
        httpResponse.response200Header(body.length, contentType);
        httpResponse.responseBody(body);
    }


}
