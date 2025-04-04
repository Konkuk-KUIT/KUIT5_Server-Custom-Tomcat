package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import model.User;
import model.UserParam;
import webserver.Route;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try {
            String body = request.getBody();
            System.out.println("로그인 body: " + body);

            Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

            String userId = params.get(UserParam.USER_ID.key());
            String password = params.get(UserParam.PASSWORD.key());

            User user = MemoryUserRepository.getInstance().findUserById(userId);

            if (user != null && user.getPassword().equals(password)) {
                response.redirectWithCookie(Route.INDEX.path(), "logined=true");
                System.out.println("로그인 성공: " + userId);
            } else {
                response.redirect(Route.LOGIN_FAILED.path());
                System.out.println("로그인 실패: " + userId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
