package controller;

import db.MemoryUserRepository;
import http.enums.UrlPath;
import http.model.HttpRequest;
import http.model.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;
import model.UserParamKey;

import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        String userId = params.get(UserParamKey.USER_ID.key());
        String password = params.get(UserParamKey.PASSWORD.key());
        User user = MemoryUserRepository.getInstance().findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response.sendRedirectWithCookie(UrlPath.INDEX.getPath(), "logined=true");
        } else {
            response.sendRedirect(UrlPath.LOGIN_FAIL.getPath());
        }
    }
}