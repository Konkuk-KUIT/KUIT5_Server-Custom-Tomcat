package controller;

import http.constants.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.request.RequestURL.LOGIN;
import static http.request.RequestURL.USER_LIST_HTML;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookie = request.getHeader(HttpHeader.COOKIE);
        if (cookie == null || !cookie.equals("logined=true")) {
            response.redirect(LOGIN.getUrl());
            return;
        }
        response.forward(USER_LIST_HTML.getUrl());
    }
}