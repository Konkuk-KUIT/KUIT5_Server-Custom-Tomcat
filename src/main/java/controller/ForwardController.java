package controller;

import model.HttpRequest;
import model.HttpResponse;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward(httpRequest.getURI().getPath());
    }
}
