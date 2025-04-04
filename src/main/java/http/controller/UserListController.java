package http.controller;

import http.HttpRequest;
import http.HttpResponse;
import http.httpEnum.UrlPath;
import http.util.HttpRequestUtils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class UserListController implements Controller {
    private static final Logger log = Logger.getLogger(UserListController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> headers = request.getHeaderLine();

        if(!isLoggedIn(headers)){
            log.info("로그인하지 않은 사용자의 userList 접근");
            response.redirect(UrlPath.LOGIN_HTML.getPath());
            return;
        }

        log.info("로그인한 사용자의 userList 접근");
        response.redirect(UrlPath.USER_LIST_HTML.getPath());
    }

    private boolean isLoggedIn(Map<String, String> headers) {
        String cookieLine = headers.get("Cookie");
        if (cookieLine == null) {
            return false;
        }

        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieLine);
        return "true".equals(cookies.get("logined"));
    }
}

