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

public class SignUpController implements Controller {
    private static final Logger log = Logger.getLogger(SignUpController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());

        String userId = params.get(UserQueryKey.USER_ID.getKey());
        String password = params.get(UserQueryKey.PASSWORD.getKey());
        String name = params.get(UserQueryKey.NAME.getKey());
        String email = params.get(UserQueryKey.EMAIL.getKey());

        if (ValidationUtils.isAnyBlank(userId, password, name, email)) { // 비어있는 값 존재하면 다시 입력 받도록 리턴
            response.redirect(UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        User user = new User(userId, password, name, email);
        MemoryUserRepository.getInstance().addUser(user);

        log.info("New user signed up: " + user.getUserId());
        response.redirect(UrlPath.INDEX.getPath());
    }
}

