package webserver.controller;

import constant.HttpHeader;
import constant.URL;
import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class LoginController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> parameter = parseQueryParameter(httpRequest.getBody());
        String userId = parameter.get("userId");
        String password = parameter.get("password");

        User userById = MemoryUserRepository.getInstance().findUserById(userId);
        if(userById == null) {
            httpResponse.redirect(URL.LOGIN_FAILED.getUrl(), null);
            return;
        }
        if(userById.getPassword().equals(password)) {
            httpResponse.redirect(URL.HOME_URL.getUrl(), "logined=true");
            return;
        }
        httpResponse.redirect(URL.LOGIN_FAILED.getUrl(), null);
    }
}
