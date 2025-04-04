package controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.HttpRequest;
import model.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static enums.URL.INDEX_HTML;
import static enums.URL.LOGIN_FAILED_HTML;
import static enums.UserKey.ID;
import static enums.UserKey.PW;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String query = httpRequest.getURI().getQuery();
        Map<String, String> map = HttpRequestUtils.parseQueryParameter(query);

        if(validUser(map)){
            loginSuccess(httpResponse);
            return;
        }
        loginFailed(httpResponse);
    }

    public static boolean validUser(Map<String, String> map) {
        String userId = map.get(ID.getKey());
        String password = map.get(PW.getKey());

        User user = MemoryUserRepository.getInstance().findUserById(userId);
        if(user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    private static void loginSuccess(HttpResponse httpResponse) throws IOException {
        httpResponse.redirect(INDEX_HTML.getUrl(), true);
        System.out.println("로그인 성공!");
    }

    private static void loginFailed(HttpResponse httpResponse) throws IOException {
        httpResponse.redirect(LOGIN_FAILED_HTML.getUrl(), false);
        System.out.println("로그인 실패");
    }
}
