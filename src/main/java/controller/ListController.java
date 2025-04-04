package controller;

import enumClass.UrlPath;
import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static enumClass.UrlPath.*;

public class ListController implements Controller{

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        if(checkLoginCookie(httpRequest.getHeaders())){
            httpResponse.send200Html(USER_LIST.getPath());
            return;
        }

        httpResponse.send302(LOGIN_PAGE.getPath());
    }

    private boolean checkLoginCookie(Map<String, String> headers){
        return headers.containsKey("Cookie") && headers.get("Cookie").equals("logined=true");
    }
}
