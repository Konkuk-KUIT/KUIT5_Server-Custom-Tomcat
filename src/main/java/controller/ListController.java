package controller;

import data.Header;
import data.UrlData;
import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;
import java.util.Objects;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookie = request.headers.get(Header.COOKIE.get());
        if(Objects.equals(cookie, Header.LOGIN.get())){
            response.forward(UrlData.GO_LIST_URL.getData());
        }
        else{
            response.redirect(UrlData.LOGIN_URL.getData());
        }

    }
}
