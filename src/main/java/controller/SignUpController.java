package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import http.util.HttpMethod;
import model.User;
import model.UserParam;
import webserver.Route;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> params;

            // 수정한 부분
            if (HttpMethod.isEqual(request.getMethod(), HttpMethod.GET)) {
                params = HttpRequestUtils.parseQueryParameter(request.getQueryString());
            } else {
                params = HttpRequestUtils.parseQueryParameter(request.getBody());
            }

            User user = new User(
                    params.get(UserParam.USER_ID.key()),
                    params.get(UserParam.PASSWORD.key()),
                    params.get(UserParam.NAME.key()),
                    params.get(UserParam.EMAIL.key())
            );

            MemoryUserRepository.getInstance().addUser(user);
            System.out.println("회원가입 성공: " + user.getUserId());

            response.redirect(Route.INDEX.path());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
