package webserver.controller;

import constant.HttpHeader;
import constant.URL;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookieValue = httpRequest.getHeader(HttpHeader.COOKIE.getHeader());
        if("true".equals(getParseCookie(cookieValue).get("logined"))){
            httpResponse.redirect(URL.USER_LIST.getUrl(), null);
            return;
        }
        httpResponse.redirect(URL.LOGIN.getUrl(), null);
    }
    private static Map<String, String> getParseCookie(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();
        if(cookieHeader == null || cookieHeader.isEmpty()) return cookies;

        String[] pairs = cookieHeader.split("; ");
        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2){
                cookies.put(keyValue[0], keyValue[1]);
            }
        }
        return cookies;
    }
}
