package controller;

import http.enums.UrlPath;
import http.model.HttpRequest;
import http.model.HttpResponse;

public class UserListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        boolean isLogined = request.getHeaders().getOrDefault("Cookie", "").contains("logined=true");

        if (isLogined) {
            response.forward("/user/list.html");
        } else {
            response.sendRedirect(UrlPath.LOGIN_PAGE.getPath());
        }
    }
}