package controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.HttpRequest;
import model.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static enums.HttpMethod.GET;
import static enums.HttpMethod.POST;
import static enums.URL.INDEX_HTML;
import static enums.UserKey.*;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> map = null;
        String method = httpRequest.getMethod();
        String query = null;

        if(method.equals(GET.getMethod())) {
            query = httpRequest.getURI().getQuery();
        }
        if(method.equals(POST.getMethod())) {
            query = httpRequest.getBody();
        }
        map = HttpRequestUtils.parseQueryParameter(query);

        signUp(httpResponse, mapToUser(map));
    }

    public static User mapToUser(Map<String, String> map) {
        String userId = map.get(ID.getKey());
        String password = map.get(PW.getKey());
        String name = map.get(NAME.getKey());
        String email = map.get(EMAIL.getKey());

        User user = new User(userId, password, name, email);
        return user;
    }

    private static void signUp(HttpResponse httpResponse, User user) throws IOException {
        MemoryUserRepository.getInstance().addUser(user);
        System.out.println(user.getUserId()+" 회원가입 완료");

        httpResponse.redirect(INDEX_HTML.getUrl(), false);
    }
}
