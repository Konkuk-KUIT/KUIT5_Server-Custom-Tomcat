package controller;

import db.MemoryUserRepository;
import db.Repository;
import enumClass.HttpMethod;
import enumClass.UrlPath;
import enumClass.UserQueryKey;
import http.util.HttpRequestUtils;
import http.util.httpRequest.HttpRequest;
import http.util.httpResponse.HttpResponse;
import model.User;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.Map;

import static enumClass.HttpMethod.*;
import static enumClass.UrlPath.*;
import static enumClass.UserQueryKey.*;

public class SignUpController implements Controller{

    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        Map<String, String> bodyMap = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        User user = new User(bodyMap.get(USER_ID.getKey()),
                bodyMap.get(PASSWORD.getKey()),
                bodyMap.get(NAME.getKey()),
                bodyMap.get(EMAIL.getKey()));
        repository.addUser(user);

        httpResponse.send302(ROOT.getPath());


    }


}
