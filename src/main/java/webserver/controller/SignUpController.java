package webserver.controller;

import constant.URL;
import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> parameter = parseQueryParameter(httpRequest.getBody());
        User user = new User(
                parameter.get("userId"),
                parameter.get("password"),
                parameter.get("name"),
                parameter.get("email")
        );

        MemoryUserRepository.getInstance().addUser(user);
        System.out.println(MemoryUserRepository.getInstance().findUserById(parameter.get("userId")));
        httpResponse.redirect(URL.HOME_URL.getUrl(), null);
    }
}
