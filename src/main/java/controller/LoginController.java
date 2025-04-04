package controller;

import data.UrlData;
import data.UserData;
import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        String userId = params.get(UserData.ID.getData());

        String password = params.get(UserData.Password.getData());
        User now = MemoryUserRepository.getInstance().findUserById(userId);
        if(now != null &&now.getPassword().equals(password)){
            response.cookieRedirect(UrlData.DEFAULT_URL.getData());
            return;
        }
        response.redirect(UrlData.LOGIN_FAIL_URL.getData());

    }
}
