package http.controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.File;
import java.io.IOException;

public class StaticFileController implements Controller {

    private final String path;

    public StaticFileController(String path) {
        this.path = path;
    }

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (!isValidPath(path)) {
            response.notFound();
            return;
        }

        if (path.endsWith(".css")) {
            response.forwardCss(path);
            return;
        }

        response.forward(path);
    }

    private boolean isValidPath(String path) {
        File file = new File("./webapp" + path);
        return file.exists();
    }
}
