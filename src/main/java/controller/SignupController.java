package controller;

import db.MemoryUserRepository;
import http.enums.UrlPath;
import http.model.HttpRequest;
import http.model.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;
import model.UserParamKey;

import java.util.Map;

public class SignupController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        User user = new User(
                params.get(UserParamKey.USER_ID.key()),
                params.get(UserParamKey.PASSWORD.key()),
                params.get(UserParamKey.NAME.key()),
                params.get(UserParamKey.EMAIL.key())
        );
        MemoryUserRepository.getInstance().addUser(user);
        response.sendRedirect(UrlPath.INDEX.getPath());
    }
}