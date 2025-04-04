package http.controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.UrlPath;
import http.UserQueryKey;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class SignUpController implements Controller {
    private static final Logger log = Logger.getLogger(SignUpController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());

        User user = new User(
                params.get(UserQueryKey.USER_ID.getKey()),
                params.get(UserQueryKey.PASSWORD.getKey()),
                params.get(UserQueryKey.NAME.getKey()),
                params.get(UserQueryKey.EMAIL.getKey())
        );
        MemoryUserRepository.getInstance().addUser(user);

        log.info("New user signed up: " + user.getUserId());
        response.redirect(UrlPath.INDEX.getPath());
    }
}

