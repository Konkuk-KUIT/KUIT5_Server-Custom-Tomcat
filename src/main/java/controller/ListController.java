package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.HttpHeader;
import webserver.Route;

import java.io.IOException;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            String cookies = request.getHeaders().get(HttpHeader.COOKIE.key());
            boolean isLogined = false;

            if (cookies != null) {
                String[] cookieArray = cookies.split(";");
                for (String cookie : cookieArray) {
                    String[] pair = cookie.trim().split("=");
                    if (pair.length == 2 && pair[0].equals("logined") && pair[1].equals("true")) {
                        isLogined = true;
                        break;
                    }
                }
            }

            if (isLogined) {
                response.forward(Route.LIST.path());
            } else {
                response.redirect(Route.LOGIN.path());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
