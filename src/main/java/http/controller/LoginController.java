package http.controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.UrlPath;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

        if (user == null || !user.getPassword().equals(params.get("password"))) {
            log.info("user login failed");
            response.redirect(UrlPath.LOGIN_FAILED.getPath());
            return;
        }

        log.info("user login success!: " + user.getUserId());
        response.redirect(UrlPath.INDEX.getPath(), "logined=true");
    }
}
