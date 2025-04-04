package http.controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.httpEnum.UrlPath;
import http.httpEnum.UserQueryKey;
import http.util.HttpRequestUtils;
import http.util.ValidationUtils;
import model.User;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        String userId = params.get(UserQueryKey.USER_ID.getKey());
        String password = params.get(UserQueryKey.PASSWORD.getKey());

        if (ValidationUtils.isAnyBlank(userId, password)) { // 비어있는 값 존재하면 다시 입력 받도록 리턴
            response.redirect(UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        User user = MemoryUserRepository.getInstance().findUserById(userId);

        if (user == null || !user.getPassword().equals(password)) {
            log.info("user login failed");
            response.redirect(UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        log.info("user login success!: " + user.getUserId());
        response.redirect(UrlPath.INDEX.getPath(), "logined=true");
    }
}
